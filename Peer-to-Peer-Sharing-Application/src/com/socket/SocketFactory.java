package com.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Factory for socket implementations. Used by classes of the PeerBase
 * system to generate sockets appropriate for normal use, testing, or
 * educational purposes.
 * 
 * 
 *
 */

public abstract class SocketFactory {
	public static SocketFactory currentFactory = new NormalSocketFactory();
	public static SocketFactory getSocketFactory(){
		return currentFactory;
	}
	public static void setSocketFactory(SocketFactory sf){
		if(sf ==null){
			throw new NullPointerException("Attemping to set null socket factory");	
		}
		currentFactory = sf;
		
	}
	
	/**
	 * Constructs a new socket object, appropriate to the purpose
	 * of the factory.
	 * 
	 * @param host the host name
	 * @param port the port number
	 * @return a socket connection object
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public abstract SocketInterface makeSocket(String host,int port) throws IOException, UnknownHostException;
	

	/**
	 * Constructs a new SocketInterface object, encapsulating a standard Java 
	 * API Socket object.
	 * 
	 * @param socket the socket to encapsulate
	 * @return a socket connection object
	 * @throws IOException
	 */
	public abstract SocketInterface makeSocket(Socket socket)throws IOException;
}
