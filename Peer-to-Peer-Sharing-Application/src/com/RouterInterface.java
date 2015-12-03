package com;

/**
 * Interface for objects that determine the next hop for
 * a message to be routed to in the peer-to-peer network. Given the
 * identifier of a peer, the router object should be able to return
 * information regarding the peer node to which the message should
 * be forwarded next.
 * 

 **/

public interface RouterInterface {
	 public PeerInfo route( String peerid );
}
