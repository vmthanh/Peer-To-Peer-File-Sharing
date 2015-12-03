package com.util;

import com.Node;
import com.PeerInfo;
import com.RouterInterface;

/**
 * A simple router that attempts to route messages by simply looking
 * for the destination peer information in the node's list of peers.
 * If the destination node is not an immediate neighbor, this function
 * fails.
 * 
 * @author Nadeem Abdul Hamid
 */

public class SimpleRouter implements RouterInterface{
	private Node peer;
	
	public SimpleRouter(Node peer) {
		this.peer = peer;
	}
	
	public PeerInfo route(String peerid) {
		for (String key : peer.getPeerKeys())
			if (peer.getPeer(key).getId().equals(peerid))
				return peer.getPeer(peerid);
		return null;
	}

}
