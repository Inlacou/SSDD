package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;
import es.deusto.ingenieria.ssdd.chat.data.User;

public class ChatClientController {
	private String serverIP;
	private int serverPort;
	private User connectedUser;
	private User chatReceiver;
	private LocalObservable observable;
	
	public ChatClientController() {
		this.observable = new LocalObservable();
		this.serverIP = null;
		this.serverPort = -1;
	}
	
	public String getConnectedUser() {
		if (this.connectedUser != null) {
			return this.connectedUser.getNick();
		} else {
			return null;
		}
	}
	
	public String getChatReceiver() {
		if (this.chatReceiver != null) {
			return this.chatReceiver.getNick();
		} else {
			return null;
		}
	}
	
	public String getServerIP() {
		return this.serverIP;
	}
	
	public int gerServerPort() {
		return this.serverPort;
	}
	
	public boolean isConnected() {
		return this.connectedUser != null;
	}
	
	public boolean isChatSessionOpened() {
		return this.chatReceiver != null;
	}
	
	public void addLocalObserver(Observer observer) {
		this.observable.addObserver(observer);
	}
	
	public void deleteLocalObserver(Observer observer) {
		this.observable.deleteObserver(observer);
	}
	
	public boolean connect(String ip, int port, String nick) {
		
		//TODO ENTER YOUR CODE TO CONNECT
		
		this.connectedUser = new User();
		this.connectedUser.setNick(nick);
		this.serverIP = ip;
		this.serverPort = port;
		
		String message = "000 INIT " + nick;
		
		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			
			udpSocket.receive(reply);
			String response = new String(reply.getData());
			String s = response.trim();
			
			udpSocket.close();
			
			response = response.substring(0, 3);
			
			if (response.equals("001")) {

				s = s.substring(12);
				int portToListen = Integer.parseInt(s);
				
//				try (DatagramSocket newUdpSocket = new DatagramSocket(portToListen)) {
					
//					Runnable processServer = new ClientThread(this, newUdpSocket);
					Runnable processServer = new ClientThread(this, portToListen);
					Thread thread = new Thread(processServer);
					thread.start();
					
					return true;
					
//				} catch (SocketException e) {
//					System.err.println("# UDPClient Socket error: " + e.getMessage());
//					e.printStackTrace();
//					return false;
//				}
			}
			else if (response.equals("002")) {
				//TODO error nickname used
			}
			else if (response.equals("003")){
				//TODO error nickname not allowed
			}
			else {
				//TODO error ip already in use
			}
		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
		return false;
	}
	
	public boolean disconnect() {
		
		//TODO ENTER YOUR CODE TO DISCONNECT
		
		this.connectedUser = null;
		this.chatReceiver = null;
		
		return true;
	}
	
	public List<String> getConnectedUsers() {
		List<String> connectedUsers = new ArrayList<>();
		
		//TODO ENTER YOUR CODE TO OBTAIN THE LIST OF CONNECTED USERS
		connectedUsers.add(connectedUser.getNick());
		
		
		
		return connectedUsers;
	}
	
	public boolean sendMessage(String message) {
		
		//TODO ENTER YOUR CODE TO SEND A MESSAGE
		
		return true;
	}
	
	public void receiveMessage() {
		
		//TODO ENTER YOUR CODE TO RECEIVE A MESSAGE
		
		String message = "Received message";		
		
		//Notify the received message to the GUI
		this.observable.notifyObservers(message);
	}	
	
	public boolean sendChatRequest(String to) {
		
		//TODO ENTER YOUR CODE TO SEND A CHAT REQUEST
		
		this.chatReceiver = new User();
		this.chatReceiver.setNick(to);
		
		return true;
	}	
	
	public void receiveChatRequest() {
		
		//TODO ENTER YOUR CODE TO RECEIVE A CHAT REQUEST
		
		String message = "Chat request details";
		
		//Notify the chat request details to the GUI
		this.observable.notifyObservers(message);
	}
	
	public boolean acceptChatRequest() {
		
		//TODO ENTER YOUR CODE TO ACCEPT A CHAT REQUEST
		
		return true;
	}
	
	public boolean refuseChatRequest() {
		
		//TODO ENTER YOUR CODE TO REFUSE A CHAT REQUEST
		
		return true;
	}	
	
	public boolean sendChatClosure() {
		
		//TODO ENTER YOUR CODE TO SEND A CHAT CLOSURE
		
		this.chatReceiver = null;
		
		return true;
	}
	
	public void receiveChatClosure() {
		
		//TODO ENTER YOUR CODE TO RECEIVE A CHAT REQUEST
		
		String message = "Chat request details";
		
		//Notify the chat request details to the GUI
		this.observable.notifyObservers(message);
	}
	
	public void userConnected(String userNick) {
		
		this.observable.notifyObservers("102 NEWUSER " + userNick);
	}
	
	public void userDisconnected(String userNick) {
		
		this.observable.notifyObservers("103 LEFTUSER " + userNick);
	}
}