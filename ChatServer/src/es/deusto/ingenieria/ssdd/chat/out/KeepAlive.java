package es.deusto.ingenieria.ssdd.chat.out;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.User;

public class KeepAlive {

	private int ms;
	private ArrayList<User> users;
	DatagramPacket request;
	DatagramSocket udpSocket;
	byte[] buffer;
	
	public KeepAlive(int ms, ArrayList<User> users, DatagramSocket udpSocket){
		this.ms = ms;
		this.udpSocket = udpSocket;
	}
	
	public void run(){
		while(true){
			try {
				int numUsers = users.size();
				for (int i = 0; i <numUsers; i++) {
					sendKeepAlive(users.get(i));
				}
				wait(ms);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void sendKeepAlive(User user) throws IOException{
		DatagramPacket reply = new DatagramPacket("999 KEEPALIVE".getBytes(), "999 KEEPALIVE".getBytes().length, InetAddress.getByName(user.getIP()), Integer.parseInt(user.getPort()));
		udpSocket.send(reply);
	}
	
}
