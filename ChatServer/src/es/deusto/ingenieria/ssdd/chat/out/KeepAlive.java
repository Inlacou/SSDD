package es.deusto.ingenieria.ssdd.chat.out;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.User;

public class KeepAlive extends Thread {

	private int ms;
	private ArrayList<User> users;
	DatagramSocket udpSocket;
	
	public KeepAlive(int ms, ArrayList<User> users, DatagramSocket udpSocket){
		this.ms = ms;
		this.users = users;
		this.udpSocket = udpSocket;
	}
	
	@Override
	public void run(){
		System.out.println("KeepAlive service started");
		while(true){
			try {
				int numUsers = users.size();
				for (int i = 0; i < numUsers; i++) {
					sendKeepAlive(users.get(i));
				}
				Thread.sleep(ms);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void sendKeepAlive(User user) throws IOException{
		String ip = user.getIP();
		String message = "999 KEEPALIVE";
		int port = user.getPort();
		System.out.println(" * Sending a message to '" + ip + "' Port: "+port+" -> " + message);
		while(message.getBytes().length<1024){
			message += " ";
		}
		DatagramPacket reply = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName(ip), port);
		udpSocket.send(reply);
		System.out.println(" - Sent a message to '" + ip + "' Port: "+port+" -> " + new String(reply.getData()));
	}
	
}
