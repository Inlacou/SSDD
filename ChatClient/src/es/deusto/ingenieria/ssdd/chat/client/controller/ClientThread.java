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
//	private DatagramSocket udpSocket;
	private int portToListen;

//	public ClientThread(ChatClientController controller, DatagramSocket udpSocket) {
	public ClientThread(ChatClientController controller, int portToListen) {
		this.controller = controller;
//		this.udpSocket = udpSocket;
		this.portToListen = portToListen;
		this.stop = false;
		this.buffer = new byte[1024];
	}

	@Override
	public void run() {

		while (!stop) {

			try (DatagramSocket udpSocket = new DatagramSocket(portToListen)) {
				System.out.println("EMPIEZA EL HILO");
				reply = new DatagramPacket(buffer, buffer.length);
				System.out.println("CLIENTE ESCUCHANDO EN EL PUERTO " + portToListen);
				udpSocket.receive(reply);
				System.out.println("MENSAJE RECIBIDO");
				message = new String(reply.getData());
				message = message.trim();
				
				if ((message.substring(0, 3)).equals("102")) {
					//TODO 102 NEWUSER new_user
					String nick = message.substring(12, message.length());
					System.out.println("NUEVO USUARIO CONECTADO: "+nick);
					controller.userConnected(nick);
				}

				else if ((message.substring(0, 3)).equals("103")) {
					//TODO 103 LEFTUSER left_user
					String nick = message.substring(13, message.length());
					controller.userDisconnected(nick);
				}

				else if ((message.substring(0, 3)).equals("212")) {
					//TODO 212 SENDMSG xxx text
				}

				else if ((message.substring(0, 3)).equals("300")) {
					//TODO 300 LEAVECHAT
				}

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
