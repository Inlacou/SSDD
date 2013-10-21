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
					reSendMessage(msgr);
				}
			}
		}
	}

	public void receivedMessage(String text, String ip){
		MensajeReenviable msgr;
		int numeroMensajes = mensajes.size();
		for (int i = 0; i < numeroMensajes; i++) {
			msgr = mensajes.get(i);
			if(msgr.getIpEnvio().equals(ip)){
				ArrayList<String> textos = msgr.getTextosRecepcion();
				int numeroTextos = textos.size();
				for (int j = 0; j < numeroTextos; j++) {
					String texto = textos.get(i);
					if(texto.trim().equals(text.trim())){
						if(texto.equals("202 CHAT ACCEPTED") || texto.equals("203 CHAT REJECTED")){
							sendMessage(texto, msgr.getIpRecepcion(), msgr.getPortRecepcion());
						}
						textos.remove(i);
						break;
					}
				}
			}
		}
	}

	public void registerMessage(String string, String ip, int port,
			String string2, String ip2, int port2) {
		mensajes.add(new MensajeReenviable(string, ip, port, string2, ip2, port2, System.currentTimeMillis()));
	}
	
	public void registerMessage(String string, String ip, int port,
			String string2, String string3, String ip2, int port2) {
		ArrayList<String> textos = new ArrayList<String>();
		textos.add(string2);
		textos.add(string3);
		mensajes.add(new MensajeReenviable(string, ip, port, textos, ip2, port2, System.currentTimeMillis()));
	}

	public void reSendMessage(MensajeReenviable msgr){
		handler.sendMessage(msgr.getText(), msgr.getIpEnvio(), msgr.getPortEnvio());
	}
	
	public void sendMessage(String texto, String ip, int port){
		handler.sendMessage(texto, ip, port);
	}

	public void registerMessage(String string, String ip, int port,
			String string2, String string3, String ip2) {

	}

}
