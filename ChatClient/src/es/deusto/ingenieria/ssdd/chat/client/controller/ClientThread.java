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
	private ChatClientController controller;
	
	public ClientThread(DatagramSocket udpSocket, InetAddress serverHost, int serverPort, ChatClientController controller) {
		this.udpSocket = udpSocket;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.controller = controller;
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
				if ((response.substring(0, 3)).equals("102")) {
					//102 NEWUSER new_user
					String nick = response.substring(12, response.length());
					controller.userConnected(nick);
				}
				
				else if ((response.substring(0, 3)).equals("103")) {
					//103 LEFTUSER left_user
					String nick = response.substring(13, response.length());
					controller.userDisconnected(nick);
				}
				
				else if ((response.substring(0, 3)).equals("212")) {
					//212 SENDMSG xxx text
				}
				
				else if ((response.substring(0, 3)).equals("300")) {
					//300 LEAVECHAT
				}
				
				else if ((response.substring(0, 3)).equals("666")) {
					//666 ERROR NOT LOGGED IN
				}
				
				else if ((response.substring(0, 3)).equals("999")) {
					//999 KEEPALIVE
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
