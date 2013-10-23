package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import es.deusto.ingenieria.ssdd.chat.keepalive.KeepAlive;

public class ClientThread implements Runnable {

	private boolean stop;
	private String message;
	private DatagramPacket reply;
	private ChatClientController controller;
	private byte[] buffer;
	private int portToListen;

	public ClientThread(ChatClientController controller, int portToListen) {
		this.controller = controller;
		this.portToListen = portToListen;
		this.stop = false;
		this.buffer = new byte[1024];
	}

	@Override
	public void run() {
		
		KeepAlive ka = new KeepAlive(this);
		ka.start();

		while (!stop) {

			try (DatagramSocket udpSocket = new DatagramSocket(portToListen)) {
				
				this.buffer = new byte[1024];
				reply = new DatagramPacket(buffer, buffer.length);
				System.out.println("Listening in the port " + portToListen);
				udpSocket.receive(reply);
				
				message = new String(reply.getData());
				message = message.trim();
				System.out.println("Received a message -> " + message);
				
				if ((message.substring(0, 3)).equals("100")) {
					controller.addMessageToArray(message.substring(9));
				}
				
				else if ((message.substring(0, 3)).equals("102")) {
					String nick = message.substring(12, message.length());
					controller.userConnected(nick);
				}

				else if ((message.substring(0, 3)).equals("103")) {
					String nick = message.substring(13, message.length());
					controller.userDisconnected(nick);
				}
				
				else if ((message.substring(0, 3)).equals("200")) {
					String nick = message.substring(13, message.length());
					controller.receiveChatRequest(nick);
				}

				else if ((message.substring(0, 3)).equals("210")) {
					String msg = message.substring(12, message.length());
					controller.receiveMessage(msg);
				}

				else if ((message.substring(0, 3)).equals("300")) {
					controller.receiveChatClosure();
				}

				else if ((message.substring(0, 3)).equals("666")) {
					stop = true;
					ka.stopThread();
					controller.notLoggedIn666Error();
				}

				else if ((message.substring(0, 3)).equals("999")) {
					ka.setLastKeepAlive();
				}
				
			} catch (IOException e) {
				stop = true;
				ka.stopThread();
			}
		}
	}
	
	public void stopThread() {
		this.stop = true;
		controller.serverIsDown();
	}
}
