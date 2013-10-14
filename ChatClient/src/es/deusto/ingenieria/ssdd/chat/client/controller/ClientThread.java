package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientThread implements Runnable {
	
	private DatagramSocket udpSocket;
	private InetAddress serverHost;
	private int serverPort;
	private boolean stop;
	private String message;
	private String response;
	private DatagramPacket request;
	private DatagramPacket reply;
	
	public ClientThread(DatagramSocket udpSocket, InetAddress serverHost, int serverPort) {
		this.udpSocket = udpSocket;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.stop = false;
		byte[] buffer = new byte[1024];
		this.reply = new DatagramPacket(buffer, buffer.length);
	}

	@Override
	public void run() {
		
		while (!stop) {
			
			try {
				
				udpSocket.receive(reply);
				response = new String(reply.getData());
				
				//TODO evaluar respuesta
				if ((response.substring(0, 3)).equals("102 103 201 202 203 ")) {
					
				}
				
			} catch (SocketException e) {
				System.err.println("# UDPClient Socket error: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("# UDPClient IO error: " + e.getMessage());
			}
		}
	}

}
