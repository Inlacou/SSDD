package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.net.DatagramSocket;

public class ClientThread implements Runnable {
	
	private DatagramSocket udpSocket;
	private boolean stop;
	
	public ClientThread(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
		this.stop = false;
	}

	@Override
	public void run() {
		// TODO
		while (!stop) {
			
		}
	}

}
