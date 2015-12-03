package com.util;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.Node;
import com.PeerConnection;
import com.PeerMessage;
import com.StabilizerInterface;

public class SimplePingStabilizer implements StabilizerInterface{
	private Node peer;
	private String msgtype;
	
	public SimplePingStabilizer(Node peer) {
		this(peer, "PING");
	}
	
	public SimplePingStabilizer(Node peer, String msgtype) {
		this.peer = peer;
		this.msgtype = msgtype;
	}
	
	public void stabilizer() {
		List<String> todelete = new ArrayList<String>();
		for (String pid : peer.getPeerKeys()) {
			boolean isconn = false;
			PeerConnection peerconn = null;
			try {
				peerconn = new PeerConnection(peer.getPeer(pid));
				peerconn.sendData(new PeerMessage(msgtype, ""));
				isconn = true;
			}
			catch (IOException e) {
				todelete.add(pid);
			}
			if (isconn)
				peerconn.close();
		}
		
		for (String pid : todelete) {
			peer.removePeer(pid);
		}
	}

}
