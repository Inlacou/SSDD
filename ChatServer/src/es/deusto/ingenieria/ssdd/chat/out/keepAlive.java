package es.deusto.ingenieria.ssdd.chat.out;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.User;

public class keepAlive {

	private int ms;
	private ArrayList<User> users;
	DatagramPacket request, reply;
	DatagramSocket udpSocket;
	byte[] buffer;
	
	public keepAlive(int ms, ArrayList<User> users){
		this.ms = ms;
	}
	
	public void run(){
		while(true){
			try {
				int numUsers = users.size();
				for (int i = 0; i <numUsers; i++) {
					sendMessage(users.get(i));
				}
				wait(ms);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void sendMessage(User user) throws IOException{
		reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
		udpSocket.send(reply);
	}
	
}
