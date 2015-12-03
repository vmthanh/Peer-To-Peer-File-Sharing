package com.socket;

import java.io.IOException;

/**
 * The socket interface for the PeerBase system. Methods for reading
 * and writing are modelled after the basic InputStream/OutputStream
 * classes of the Java library.
 * 
 * 
 *
 */

public interface SocketInterface {
	/** Writes b.length bytes from the specified byte array to this 
	 * socket connection. 
	 * 
	 * @param b the data
	 * @throws IOException if an I/O error occurs
	 **/
	public void write(byte[] b) throws IOException;
	
	
	/**
	 * Reads the next byte of data from the socket connection. The value 
	 * byte is returned as an int in the range 0 to 255. If no byte 
	 * is available because the end of the input has been reached, 
	 * the value -1 is returned. This method blocks until input data 
	 * is available, the end of the input is detected, or an exception 
	 * is thrown.
	 * @return the next byte of data, or -1 if the end of the input is
	 *         reached
	 * @throws IOException if an I/O error occurs
	 */
	public int read() throws IOException;
	
	
	/**
	 * Reads some number of bytes from the socket connection and stores them 
	 * into the buffer array b. The number of bytes actually read is 
	 * returned as an integer. This method blocks until input data is 
	 * available, end of file is detected, or an exception is thrown.
	 * 
	 * @see InputStream#read()
	 * 
	 * @param b the buffer into which the data is read
	 * @return the total number of bytes read into the buffer, or -1 if
	 * there is no more data because the end of the input has been reached
	 * @throws IOException if an I/O error occurs
	 */
	public int read(byte[] b) throws IOException;
	
	
	/**
	 * Closes this connection and releases any system resources 
	 * associated with the socket.
	 * 
	 * @throws IOException if an I/O error occurs
	 **/
	public void close() throws IOException;
}
