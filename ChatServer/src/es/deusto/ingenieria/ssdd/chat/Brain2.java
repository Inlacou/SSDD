package es.deusto.ingenieria.ssdd.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.Mensaje;

public class Brain2 extends Thread {

	ArrayList<Mensaje> mensajes;
	DatagramPacket request;
	DatagramSocket udpSocket;
	byte[] buffer;
	
	public Brain2(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}

	@Override
	public void run(){
		System.out.println("KeepAlive service started");
		while(true){
			
		}
	}

	public void registerMessage(String string, String ip, int port,
			String string2, String ip2) {
		mensajes.add(new Mensaje());
	}
	
}
