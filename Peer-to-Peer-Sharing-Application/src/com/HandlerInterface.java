package com;

/**
 * The interface for handling a new, incoming connection from a peer.
 * When a peer connects and sends a message to this node, the resulting 
 * connection is encapsulated in a PeerConnection object and passed,
 * along with the message, to an object of a class that implements this
 * interface. This dispatch is carried out by the Node object with
 * which the handler has been registered. Messages are registered and
 * dispatched according to their type.
 * 
 *
 **/

public interface HandlerInterface {
	
	/*Called when a peer connects and sends a message to this node*/
	public void handleMessage(PeerConnection peerconn, PeerMessage msg);
}
