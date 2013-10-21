package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

		while (!stop) {

			try (DatagramSocket udpSocket = new DatagramSocket(portToListen)) {
				
				reply = new DatagramPacket(buffer, buffer.length);
				udpSocket.receive(reply);
				
				message = new String(reply.getData());
				message = message.trim();
				
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
				
//				else if ((message.substring(0, 3)).equals("211")) {
//					//TODO 211 RECMSG XXX (en el controller)
//				}

//				else if ((message.substring(0, 3)).equals("212")) {
//					//TODO 212 SENDMSG XXX text
//				}

//				else if ((message.substring(0, 3)).equals("300")) {
//					//TODO 300 LEAVECHAT
//				}

//				else if ((message.substring(0, 3)).equals("301")) {
//					//TODO 301 LEAVECHAT OK
//				}
				
//				else if ((message.substring(0, 3)).equals("401")) {
//					//TODO 401 LEAVEAPP OK
//				}

				else if ((message.substring(0, 3)).equals("666")) {
					//TODO 666 ERROR NOT LOGGED IN
				}

				else if ((message.substring(0, 3)).equals("999")) {
					//TODO 999 KEEPALIVE
				}
				
			} catch (IOException e) {
				stop = true;
			}
		}
	}
}
