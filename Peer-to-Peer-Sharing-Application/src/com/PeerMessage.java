package com;

import java.io.IOException;

import com.socket.SocketInterface;

/**
 * Represents a message, composed of type and data fields, in the PeerBase
 * system. Also provides functionality for converting messages to/from
 * byte arrays in a portable manner. The type of every message is a 4-byte
 * value (i.e. 4-character string).
 * 
 * 
 */

public class PeerMessage {
	
	private byte[] type;
	private byte[] data;
	
	/**
	 * Constructs a new PeerMessage object.
	 * @param type the message type (4 bytes)
	 * @param data the message data
	 */
	public PeerMessage(byte[] type, byte[] data) {
		this.type = (byte[])type.clone();
		this.data = (byte[])data.clone();
	}
	
	
	/** 
	 * Constructs a new PeerMessage object.
	 * @param type the message type (4 characters)
	 * @param data the message data
	 */
	public PeerMessage(String type, String data) {
		this(type.getBytes(), data.getBytes());
	}
	
	
	/**
	 * Constructs a new PeerMessage object.
	 * @param type the message type (4 characters)
	 * @param data the message data
	 */
	public PeerMessage(String type, byte[] data) {
		this(type.getBytes(), data);
	}
	
	
	/**
	 * Constructs a new PeerMessage object by reading data
	 * from the given socket connection.
	 * @param s a socket connection object
	 * @throws IOException if I/O error occurs
	 */
	public PeerMessage(SocketInterface s) throws IOException {
		type = new byte[4];
		byte[] thelen = new byte[4]; // for reading length of message data
		if (s.read(type) != 4)
			throw new IOException("EOF in PeerMessage constructor: type");
		if (s.read(thelen) != 4)
			throw new IOException("EOF in PeerMessage constructor: thelen");
		
		int len = byteArrayToInt(thelen);
		data = new byte[len];
		
		if (s.read(data) != len)
			throw new IOException("EOF in PeerMessage constructor: " +
									"Unexpected message data length");
	}
	
	/** 
	 * Returns the message type as a String.
	 * @return the message type (4-character String)
	 */
	public String getMsgType() {
		return new String(type);
	}
	
	
	/**
	 * Returns the message type.
	 * @return the message type (4-byte array)
	 */
	public byte[] getMsgTypeBytes() {
		return (byte[])data.clone();
	}
	
	/**
	 * Returns the message data as a String.
	 * @return the message data
	 */
	public String getMsgData() {
		return new String(data);
	}

	
	/**
	 * Returns the message data.
	 * @return the message data
	 */
	public byte[] getMsgDataBytes() {
		return (byte[])data.clone();
	}
	
	/**
	 * Returns a packed representation of this message as an
	 * array of bytes.
	 * @return byte array of message data
	 */
	public byte[] toBytes() {
		byte[] bytes = new byte[4 + 4 + data.length];
		byte[] lenbytes = intToByteArray(data.length);
		
		for (int i=0; i<4; i++) bytes[i] = type[i];
		for (int i=0; i<4; i++) bytes[i+4] = lenbytes[i];
		for (int i=0; i<data.length; i++) bytes[i+8] = data[i];
		
		return bytes;
	}
	
	public String toString() {
		return "PeerMessage[" + getMsgType() + ":" + getMsgData() + "]";
	}
	
	/**
	 * Returns a byte array containing the two's-complement representation of the integer.<br>
	 * The byte array will be in big-endian byte-order with a fixes length of 4
	 * (the least significant byte is in the 4th element).<br>
	 * <br>
	 * <b>Example:</b><br>
	 * <code>intToByteArray(258)</code> will return { 0, 0, 1, 2 },<br>
	 * <code>BigInteger.valueOf(258).toByteArray()</code> returns { 1, 2 }. 
	 * @param integer The integer to be converted.
	 * @return The byte array of length 4.
	 */
	public static byte[] intToByteArray (final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];
		
		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		
		return (byteArray);
	}
	

	public static int byteArrayToInt(byte[] byteArray) {
		int integer = 0;
		for (int n = 0; n < 4; n++) {
			integer = (integer << 8) | ( ((int)byteArray[n]) & 0xff );
		}
		
		return integer;
	}
	
	
	
	

}
