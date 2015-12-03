package com.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NormalSocketFactory extends SocketFactory{
	public SocketInterface makeSocket(String host, int port) throws IOException,UnknownHostException{
		return new NormalSocket(host, port);
	}
	@Override
	public SocketInterface makeSocket(Socket socket) throws IOException{
		return new NormalSocket(socket);
	}
}
