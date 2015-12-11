package com.sample;

import com.util.JLayerPlayerPausable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import javazoom.jl.decoder.JavaLayerException;
import com.LoggerUtil;
import com.PeerInfo;
import com.PeerMessage;
import com.socket.FileEvent;
import com.util.SimplePingStabilizer;

public class FileShareApp extends JFrame {

	private static final int FRAME_WIDTH = 565, FRAME_HEIGHT = 265;

	private JPanel filesPanel, peersPanel;
	private JPanel lowerFilesPanel, lowerPeersPanel;
	private DefaultListModel filesModel, peersModel;
	private JList filesList, peersList;

	private JButton fetchFilesButton, addFilesButton, searchFilesButton;
	private JButton removePeersButton, refreshPeersButton, rebuildPeersButton;

	private JTextField addTextField, searchTextField;
	private JTextField rebuildTextField;

	private FileShareNode peer;
	private SoundJLayer soundToPlay;

	private ObjectInputStream inputStream = null;
	private FileEvent fileEvent;
	private File dstFile = null;
	private FileOutputStream fileOutputStream = null;
	private final JButton btnPlay = new JButton("Play");
	private final JButton btnPause = new JButton("Pause");
	private final JButton btnStop = new JButton("Stop");
	private JButton btnRemoveFile;
	private JPanel panel;

	private FileShareApp(String initialhost, int initialport, int maxpeers, PeerInfo mypd) {
		peer = new FileShareNode(maxpeers, mypd);
		peer.buildPeers(initialhost, initialport, 2);

		fetchFilesButton = new JButton("Fetch");
		fetchFilesButton.addActionListener(new FetchListener());
		addFilesButton = new JButton("Add");
		addFilesButton.addActionListener(new AddListener());
		searchFilesButton = new JButton("Search");
		searchFilesButton.addActionListener(new SearchListener());
		removePeersButton = new JButton("Remove host");
		removePeersButton.addActionListener(new RemoveListener());
		refreshPeersButton = new JButton("Refresh");
		refreshPeersButton.addActionListener(new RefreshListener());
		rebuildPeersButton = new JButton("Rebuild");
		rebuildPeersButton.addActionListener(new RebuildListener());

		addTextField = new JTextField(15);
		searchTextField = new JTextField(15);
		rebuildTextField = new JTextField(15);

		setupFrame(this);

		(new Thread() {
			public void run() {
				peer.mainLoop();
			}
		}).start();

		new javax.swing.Timer(3000, new RefreshListener()).start();

		peer.startStabilizer(new SimplePingStabilizer(peer), 3000);
	}

	private void setupFrame(JFrame frame) {
		/*
		 * fixes the overlapping problem by using a BorderLayout on the whole
		 * frame and GridLayouts on the upper/lower panels
		 */

		frame = new JFrame("FileShareNode ID: <" + peer.getId() + ">");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());

		JPanel upperPanel = new JPanel();
		JPanel lowerPanel = new JPanel();
		upperPanel.setLayout(new GridLayout(1, 2));
		// allots the upper panel 2/3 of the frame height
		upperPanel.setPreferredSize(new Dimension(FRAME_WIDTH, (FRAME_HEIGHT * 2 / 3)));
		lowerPanel.setLayout(new GridLayout(1, 2));

		frame.setSize(600, 291);

		filesModel = new DefaultListModel();
		filesList = new JList(filesModel);
		peersModel = new DefaultListModel();
		peersList = new JList(peersModel);
		filesPanel = initPanel(new JLabel("Available Files"), filesList);
		peersPanel = initPanel(new JLabel("Peer List"), peersList);
		lowerFilesPanel = new JPanel();
		lowerPeersPanel = new JPanel();

		filesPanel.add(fetchFilesButton);
		
				btnRemoveFile = new JButton("Remove File");
				btnRemoveFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int  index = filesList.getSelectedIndex();
						String selected = filesList.getSelectedValue().toString();
						String filename = selected.substring(0, selected.indexOf(':'));
						peer.deleteLocalFile(filename);
						filesModel.remove(index);
					}
				});
				panel.add(btnRemoveFile);
		peersPanel.add(removePeersButton);
		peersPanel.add(refreshPeersButton);

		lowerFilesPanel.add(addTextField);
		lowerFilesPanel.add(addFilesButton);
		lowerFilesPanel.add(searchTextField);
		lowerFilesPanel.add(searchFilesButton);

		lowerPeersPanel.add(rebuildTextField);
		lowerPeersPanel.add(rebuildPeersButton);

		upperPanel.add(filesPanel);
		upperPanel.add(peersPanel);
		lowerPanel.add(lowerFilesPanel);
		lowerPanel.add(lowerPeersPanel);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selected = filesList.getSelectedValue().toString();
				String filename = selected.substring(0, selected.indexOf(':'));
				soundToPlay = new SoundJLayer(filename);
				soundToPlay.play();
			}
		});
		lowerPeersPanel.add(btnPlay);
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				soundToPlay.pauseToggle();
			}
		});
		lowerPeersPanel.add(btnPause);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				soundToPlay.stop();
			}
		});
		lowerPeersPanel.add(btnStop);

		/*
		 * by using a CENTER BorderLayout, the overlapping problem is fixed:
		 * http
		 * ://forum.java.sun.com/thread.jspa?threadID=551544&messageID=2698227
		 */

		frame.getContentPane().add(upperPanel, BorderLayout.NORTH);
		frame.getContentPane().add(lowerPanel, BorderLayout.CENTER);

		frame.setVisible(true);

	}

	private JPanel initPanel(JLabel textField, JList list) {
		panel = new JPanel();
		panel.add(textField);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(200, 105));
		panel.add(scrollPane);
		return panel;
	}

	private void updateFileList() {
		filesModel.removeAllElements();
		for (String filename : peer.getFileNames()) {
			String pid = peer.getFileOwner(filename);
			if (pid.equals(peer.getId()))
				filesModel.addElement(filename + ":(local)");
			else
				filesModel.addElement(filename + ":" + pid);
		}
	}

	private void updatePeerList() {
		peersModel.removeAllElements();
		for (String pid : peer.getPeerKeys()) {
			peersModel.addElement(pid);
		}
	}

	class FetchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (filesList.getSelectedValue() != null) {
				String selected = filesList.getSelectedValue().toString();
				String filename = selected.substring(0, selected.indexOf(':'));
				String pid = peer.getFileOwner(filename);
				String[] ownerData = pid.split(":");
				String host = ownerData[0];
				int port = Integer.parseInt(ownerData[1]);
				LoggerUtil.getLogger().fine("Fetching " + filename + " from " + host + ":" + port);
				PeerInfo pd = new PeerInfo(host, port);
				List<PeerMessage> resplist = peer.connectAndSend(pd, FileShareNode.FILEGET, filename, true);
				LoggerUtil.getLogger().fine("FETCH RESPONSE TYPE: " + resplist.get(0).getMsgType());
				if (resplist.size() > 0 && resplist.get(0).getMsgType().equals(FileShareNode.REPLY)) {
					try {
						/*
						 * FileOutputStream outfile = new
						 * FileOutputStream(filename);
						 * outfile.write(resplist.get(0).getMsgDataBytes());
						 * outfile.close(); peer.addLocalFile(filename);
						 */

						fileEvent = resplist.get(0).getFileEvent();
						if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
							throw new ClassCastException("Error occured");
						}
						String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFileName();
						if (!new File(fileEvent.getDestinationDirectory()).exists()) {
							new File(fileEvent.getDestinationDirectory()).mkdirs();
						}
						dstFile = new File(outputFile);
						fileOutputStream = new FileOutputStream(dstFile);
						fileOutputStream.write(fileEvent.getFileData());
						fileOutputStream.flush();
						fileOutputStream.close();
						peer.addLocalFile(filename);

					} catch (IOException ex) {
						LoggerUtil.getLogger().warning("Fetch error: " + ex);
					}
				}

			}
		}
	}

	class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String filename = addTextField.getText().trim();
			if (!filename.equals("")) {
				peer.addLocalFile(filename);
			}
			addTextField.requestFocusInWindow();
			addTextField.setText("");
			updateFileList();
		}
	}

	class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String key = searchTextField.getText().trim();
			for (String pid : peer.getPeerKeys()) {
				peer.sendToPeer(pid, FileShareNode.QUERY, peer.getId() + " " + key + " 4", true);
			}

			searchTextField.requestFocusInWindow();
			searchTextField.setText("");
		}
	}

	class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (peersList.getSelectedValue() != null) {
				String pid = peersList.getSelectedValue().toString();
				peer.sendToPeer(pid, FileShareNode.PEERQUIT, peer.getId(), true);
				peer.removePeer(pid);
			}
		}
	}

	class RefreshListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			updateFileList();
			updatePeerList();
		}
	}

	class RebuildListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String peerid = rebuildTextField.getText().trim();
			if (!peer.maxPeersReached() && !peerid.equals("")) {
				try {
					String[] data = peerid.split(":");
					String host = data[0];
					int port = Integer.parseInt(data[1]);
					peer.buildPeers(host, port, 3);
				} catch (Exception ex) {
					LoggerUtil.getLogger().warning("FileShareApp: rebuild: " + ex);
				}
			}
			rebuildTextField.requestFocusInWindow();
			rebuildTextField.setText("");
		}
	}

	public static void main(String[] args) throws IOException {
		int port = 9002;
		if (args.length != 1) {
			System.out.println("Usage: java ... peerbase.sample.FileShareApp <host-port>");
		} else {
			port = Integer.parseInt(args[0]);
		}

		LoggerUtil.setHandlersLevel(Level.FINE);
		new FileShareApp("localhost", 9001, 5, new PeerInfo("localhost", port));

		/*
		 * FileShareApp goo2 = new FileShareApp("localhost:8000", 5, new
		 * PeerData("localhost", 8001));
		 */
	}

}

class SoundJLayer implements Runnable {
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;
	private String namePlayerThread = "AudioPlayerThread";
	private PlaybackListener playbackListener = new PlaybackListener();

	public SoundJLayer(String filePath) {
		this.filePath = filePath;
	}

	public SoundJLayer(String filePath, String namePlayerThread) {
		this.filePath = filePath;
		this.namePlayerThread = namePlayerThread;
	}

	public void play() {
		if (this.player == null) {
			this.playerInitialize();
		} else if (!this.player.isPaused() || this.player.isComplete() || this.player.isStopped()) {
			this.stop();
			this.playerInitialize();
		}
		this.playerThread = new Thread(this, namePlayerThread);
		this.playerThread.setDaemon(true);

		this.playerThread.start();
	}

	public void pause() {
		if (this.player != null) {
			this.player.pause();

			if (this.playerThread != null) {
				// this.playerThread.stop(); //unsafe method
				this.playerThread = null;
			}
		}
	}

	public void pauseToggle() {
		if (this.player != null) {
			if (this.player.isPaused() && !this.player.isStopped()) {
				this.play();
			} else {
				this.pause();
			}
		}
	}

	public void stop() {
		if (this.player != null) {
			this.player.stop();

			if (this.playerThread != null) {
				// this.playerThread.stop(); //unsafe method
				this.playerThread = null;
			}
		}
	}

	private void playerInitialize() {
		try {
			this.player = new JLayerPlayerPausable(this.filePath);
			this.player.setPlaybackListener(this.playbackListener);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}

	// IRunnable members
	public void run() {
		try {
			this.player.resume();
		} catch (javazoom.jl.decoder.JavaLayerException ex) {
			ex.printStackTrace();
		}
	}

	private static class PlaybackListener extends JLayerPlayerPausable.PlaybackAdapter {
		// PlaybackListener members
		@Override
		public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
			System.err.println("PlaybackStarted()");
		}

		@Override
		public void playbackPaused(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
			System.err.println("PlaybackPaused()");
		}

		@Override
		public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
			System.err.println("PlaybackStopped()");
		}
	}
}
