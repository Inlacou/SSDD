package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;
import es.deusto.ingenieria.ssdd.chat.data.User;

public class ChatClientController {
	private String serverIP;
	private int serverPort;
	private User connectedUser;
	private User chatReceiver;
	private LocalObservable observable;
	private ArrayList<String> messagesArray;
	private int lastSubsequentMessage;

	public ChatClientController() {
		this.observable = new LocalObservable();
		this.serverIP = null;
		this.serverPort = -1;
		this.messagesArray = new ArrayList<String>();
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

				Runnable processServer = new ClientThread(this, portToListen);
				Thread thread = new Thread(processServer);
				thread.start();

				return true;
			}

			else
				this.observable.notifyObservers(response);
			
		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
		return false;
	}

	public boolean disconnect() {

		this.connectedUser = null;
		this.chatReceiver = null;

		String message = "400 LEAVEAPP";

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);

			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			udpSocket.receive(reply);
			String response = new String(reply.getData());

			udpSocket.close();

			response = response.substring(0, 3);

			if (response.equals("401")) {

				return true;
			}

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
		return false;
	}

	public List<String> getConnectedUsers(JFrame f) {

		List<String> connectedUsers = new ArrayList<>();

		final JDialog d = new JDialog();
		JPanel p1 = new JPanel(new GridBagLayout());
		p1.add(new JLabel("Loading users online..."), new GridBagConstraints());
		d.getContentPane().add(p1);
		d.setSize(100,100);
		d.setLocationRelativeTo(f);
		d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		d.setModal(true);
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						d.dispose();
					}
				});
			}
		};
		t.start();
		d.setVisible(true);

		connectedUsers.add(connectedUser.getNick());

		if (checkSubsequentMessages()) {

			for (int i=0; i<messagesArray.size(); i++) {

				String s = messagesArray.get(i);
				s = s.substring(7);
				String[] array = s.split(":<:");
				for (int x=0; x<array.length; x++) {

					String str = array[x];
					connectedUsers.add(str.substring(0, str.length()-3));
				}
			}
		}

		else {

			String message = "104 LISTERROR " + lastSubsequentMessage;

			try (DatagramSocket udpSocket = new DatagramSocket()) {
				InetAddress serverHost = InetAddress.getByName(serverIP);			
				byte[] byteMsg = message.getBytes();
				DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
				udpSocket.send(request);
			} catch (SocketException e) {
				System.err.println("# UDPClient Socket error: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("# UDPClient IO error: " + e.getMessage());
			}
			getConnectedUsers(f);
		}

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

	public void addMessageToArray(String message) {

		this.messagesArray.add(message);
	}

	public void restartMessagesArray() {

		this.messagesArray = new ArrayList<String>();
	}

	public boolean checkSubsequentMessages() {

		lastSubsequentMessage = -1;
		for (int i=0; i<messagesArray.size(); i++) {
			boolean stop = false;
			for (int x=0; x<messagesArray.size() && !stop; x++) {
				if (i == Integer.parseInt(messagesArray.get(x).substring(0, 3))) {
					stop = true;
					lastSubsequentMessage = i;
				}
			}
			if (!stop)
				return false;
		}

		return true;
	}
}