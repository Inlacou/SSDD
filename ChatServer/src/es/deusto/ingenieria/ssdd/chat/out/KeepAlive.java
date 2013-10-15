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
	DatagramPacket request;
	DatagramSocket udpSocket;
	byte[] buffer;
	
	public KeepAlive(int ms, ArrayList<User> users, DatagramSocket udpSocket){
		this.ms = ms;
		this.users = users;
		this.udpSocket = udpSocket;
	}
	
	@Override
	public void run(){
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
		DatagramPacket reply = new DatagramPacket("999 KEEPALIVE".getBytes(), "999 KEEPALIVE".getBytes().length, InetAddress.getByName(user.getIP()), Integer.parseInt(user.getPort()));
		udpSocket.send(reply);
	}
	
}
