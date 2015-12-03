package com;

public class PeerInfo {
	private String id;
	private String host;
	private int port;
	
	/**
	 * Creates and initializes a new PeerInfo object.
	 * 
	 * @param id this peer's (unique) identifier in the peer-to-peer system
	 * @param host the IP address
	 * @param port the TCP port number
	 */
	public PeerInfo(String id, String host, int port) {
		this.id = id;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Creates and initializes a new PeerInfo object, with the peer's
	 * identifier set to "host:port".
	 * @param host the IP address
	 * @param port the TCP port number
	 */
	public PeerInfo(String host, int port) {
		this(host + ":" + port, host, port);
	}
	
	/**
	 * Creates a PeerInfo object storing only the TCP port number.
	 */
	public PeerInfo(int port) {
		this(null, port);
	}
	
	

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	public String toString() {
		return id + " (" + host + ":" + port + ")";
	}
	
}
