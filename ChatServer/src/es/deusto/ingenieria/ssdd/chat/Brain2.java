package es.deusto.ingenieria.ssdd.chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.MensajeReenviable;

public class Brain2 extends Thread {

	ArrayList<MensajeReenviable> mensajes;
	DatagramPacket request;
	DatagramSocket udpSocket;
	byte[] buffer;
	Handler handler;
	
	public Brain2(DatagramSocket udpSocket, Handler h) {
		handler = h;
		this.udpSocket = udpSocket;
	}

	@Override
	public void run(){
		System.out.println("Reenvio de mensajes por timeout service started");
		while(true){
			int numeroMensajes = mensajes.size();
			long time = System.currentTimeMillis();
			for (int i = 0; i < numeroMensajes; i++) {
				MensajeReenviable msgr = mensajes.get(i);
				if(time-msgr.getTime()>60000){
					sendMessage(msgr);
				}
			}
		}
	}

	public void receivedMessage(String text, String ip){
		MensajeReenviable msgr;
		int numeroMensajes = mensajes.size();
		for (int i = 0; i < numeroMensajes; i++) {
			msgr = mensajes.get(i);
			if(msgr.getIpRecepcion().equals(ip) && msgr.getMensajeRecepcion().trim().equals(text.trim())){
				mensajes.remove(i);
				break;
			}
		}
	}
	
	public void registerMessage(String string, String ip, int port,
			String string2, String ip2) {
		mensajes.add(new MensajeReenviable(string, ip, port, string2, ip2, System.currentTimeMillis()));
	}
	
	public void sendMessage(MensajeReenviable msgr){
		handler.sendMessage(msgr.getText(), msgr.getIpEnvio(), msgr.getPortEnvio());
	}
	
}
