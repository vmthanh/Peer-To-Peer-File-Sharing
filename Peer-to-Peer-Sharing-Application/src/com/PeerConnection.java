package com;

import java.io.IOException;
import java.net.UnknownHostException;

import com.socket.SocketFactory;
import com.socket.SocketInterface;

/**
 * Encapsulates a socket connection to a peer, providing simple, reliable send
 * and receive functionality. All data sent to a peer through this class must be
 * formatted as a PeerMessage object.
 * 
 * 
 *
 */

public class PeerConnection {
	private PeerInfo pd;
	private SocketInterface s;

	/**
	 * Opens a new connection to the specified peer.
	 * 
	 * @param info
	 *            the peer node to connect to
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws UnknownHostException
	 */
	public PeerConnection(PeerInfo info) throws IOException, UnknownHostException {
		pd = info;
		s = SocketFactory.getSocketFactory().makeSocket(pd.getHost(), pd.getPort());
	}

	/**
	 * Constructs a connection for which a socket has already been opened.
	 * 
	 * @param info
	 * @param socket
	 */
	public PeerConnection(PeerInfo info, SocketInterface socket) {
		pd = info;
		s = socket;
	}

	/**
	 * Sends a PeerMessage to the connected peer.
	 * 
	 * @param msg
	 *            the message object to send
	 */
	public void sendData(PeerMessage msg, String type) {
		try {
			if (type == "data")
			{
				s.writeData(msg.getFileEvent());
			}else{
				s.write(msg.toBytes());
			}
			
		} catch (IOException e) {
			LoggerUtil.getLogger().warning("Error sending message: " + e);
		}
	}

	/**
	 * Receives a PeerMessage from the connected peer.
	 * 
	 * @return the message object received, or null if error
	 */
	public PeerMessage recvData() {
		try {
			PeerMessage msg = new PeerMessage(s);
			return msg;
		} catch (IOException e) {
			// it is normal for EOF to occur if there is no more replies coming
			// back from this connection.
			if (!e.getMessage().equals("EOF in PeerMessage constructor: type"))
				LoggerUtil.getLogger().warning("Error receiving message: " + e);
			else
				LoggerUtil.getLogger().finest("Error receiving message: " + e);
			return null;
		}
	}
	
	public PeerMessage recvDataGet(){
		try {
			PeerMessage msg = new PeerMessage(s,"data");
			return msg;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * Closes the peer connection.
	 */
	public void close() {
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
				LoggerUtil.getLogger().warning("Error closing: " + e);
			}
			s = null;
		}
	}

	public PeerInfo getPeerInfo() {
		return pd;
	}

	public String toString() {
		return "PeerConnection[" + pd + "]";
	}

}
