package com.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Encapsulates the standard Socket object of the Java library
 * to fit the SocketInterface of the PeerBase system.
 * 
 * 
 */

public class NormalSocket implements SocketInterface{
	private Socket s;
	private InputStream is;
	private OutputStream os;

	/**
	 * Creates a stream socket and connects it to the specified port number on the named host.
	 * 
	 * @param host the host name, or <code>null</code> for the loopback address
	 * @param port the port number
	 * @throws IOException if an I/O error occurs when creating the socket
	 * @throws UnknownHostException if the IP address of the host could not be determined
	 */
	public NormalSocket(String host, int port)throws IOException, UnknownHostException{
		this(new Socket(host, port));
		
	}

	/**
	 * Encapsulates a normal Java API Socket object.
	 * @param socket an already-open socket connection
	 * @throws IOException
	 */
	public NormalSocket(Socket socket) throws IOException{
		// TODO Auto-generated constructor stub
		s = socket;
		is = s.getInputStream();
		os = s.getOutputStream();
	}
	public void close() throws IOException{
		is.close();
		os.close();
		s.close();
	}

	@Override
	public void write(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		os.write(b);
		os.flush();
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return is.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		return is.read(b);
		
	}
	

}
