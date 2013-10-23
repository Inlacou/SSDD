package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;
import es.deusto.ingenieria.ssdd.chat.data.Message;
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
		restartMessagesArray();

		String message = "000 INIT " + nick; 

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message);
			
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			udpSocket.receive(reply);
			String response = new String(reply.getData());
			String s = response.trim();
			System.out.println("Received from the server: " + s);

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
			System.out.println("Sent to the server: " + message);

			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			udpSocket.receive(reply);
			String response = new String(reply.getData());

			udpSocket.close();

			response = response.trim();
			System.out.println("Received from the server: " + response);
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

		String message = "100 LIST";

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message);
			udpSocket.close();

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}

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

//		connectedUsers.add(connectedUser.getNick());

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

			message = "104 LISTERROR ";
			
			if (lastSubsequentMessage < 10)
				message = message + "00" + lastSubsequentMessage;
			
			else if (lastSubsequentMessage < 100)
				message = message + "0" + lastSubsequentMessage;
			
			else
				message = message + lastSubsequentMessage;

			try (DatagramSocket udpSocket = new DatagramSocket()) {
				InetAddress serverHost = InetAddress.getByName(serverIP);			
				byte[] byteMsg = message.getBytes();
				DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
				udpSocket.send(request);
				System.out.println("Sent to the server: " + message);
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

	public boolean sendMessage(String str) {

		ArrayList<byte[]> messagesArray = this.splitMessageIntoByteArrays(str);

			try (DatagramSocket udpSocket = new DatagramSocket()) {
				InetAddress serverHost = InetAddress.getByName(serverIP);
				
				for (int i=0; i<messagesArray.size(); i++) {
					
					byte[] byteMsg = messagesArray.get(i);
					DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
					udpSocket.send(request);
					System.out.println("Sent to the server: " + new String(byteMsg));
				}
				
				return true;

			} catch (SocketException e) {
				System.err.println("# UDPClient Socket error: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("# UDPClient IO error: " + e.getMessage());
			}

		return false;
	}

	public void receiveMessage(String message) {		

		Message msg = new Message();
		msg.setFrom(this.chatReceiver);
		msg.setText(message);
		msg.setTimestamp(GregorianCalendar.getInstance().getTime().getTime());
		msg.setTo(this.connectedUser);
		this.observable.notifyObservers(msg);
	}	

	public boolean sendChatRequest(String to) {

		this.chatReceiver = new User();
		this.chatReceiver.setNick(to);

		String message = "200 INITCHAT " + to;

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message);

			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			udpSocket.receive(reply);
			String response = new String(reply.getData());
			response = response.trim();
			System.out.println("Received from the server: " + response);

			response = response.substring(0, 3);

			if (response.equals("201")) {

				reply = new DatagramPacket(buffer, buffer.length);
				udpSocket.receive(reply);

				response = new String(reply.getData());
				response = response.trim();
				System.out.println("Received from the server: " + response);
				response = response.substring(0, 3);

				udpSocket.close();

				if (response.equals("202")) {

					return true;
				}

				else
					this.observable.notifyObservers(response);
			}

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}

		return false;
	}	

	public void receiveChatRequest(String nick) {

		String message = "200 INITCHAT " + nick;
		this.chatReceiver = new User();
		this.chatReceiver.setNick(nick);

		this.observable.notifyObservers(message);
	}

	public void acceptChatRequest() {

		String message = "202 CHAT ACCEPTED";

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message);

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
	}

	public void refuseChatRequest() {

		String message = "203 CHAT REJECTED";
		this.chatReceiver = null;

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message);

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
	}	

	public boolean sendChatClosure() {

		String message = "300 LEAVECHAT";

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message);

			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			udpSocket.receive(reply);
			String response = new String(reply.getData());

			udpSocket.close();

			response = response.trim();
			System.out.println("Received from the server: " + response);
			response = response.substring(0, 3);

			if (response.equals("301")) {

				this.chatReceiver = null;
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

	public void receiveChatClosure() {

		String message = "300 " + this.chatReceiver.getNick();
		this.chatReceiver = null;

		this.observable.notifyObservers(message);
		
		message = "301 LEAVECHAT OK";

		try (DatagramSocket udpSocket = new DatagramSocket()) {
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);
			System.out.println("Sent to the server: " + message); 

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
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
	
	public ArrayList<byte[]> splitMessageIntoByteArrays(String str) {
		
		String head = "210 SENDMSG ";
		String finalString = head + str;
		byte[] array = finalString.getBytes();
		ArrayList<byte[]> lastArray = new ArrayList<>();

		if (array.length > 1024) {
			int counter = 0;
			String[] words = (str.trim()).split(" ");
			do {
				finalString = head.substring(0, head.length()-1);
				byte[] arr2 = finalString.getBytes();
				boolean stop = false;
				for (int i=counter; i<words.length && !stop; i++) {
					finalString = finalString + " " + words[i];
					if (finalString.getBytes().length <= 24) {
						arr2 = finalString.getBytes();
						counter = counter + 1;
					}
					else {
						stop = true;
					}
				}
				lastArray.add(arr2);
			} while (counter < words.length);
		}

		else {
			lastArray.add(array);
		}

		return lastArray;
	}
	
	public void notLoggedIn666Error () {
		
		this.observable.notifyObservers("666");
	}
	
	public void serverIsDown() {
		
		this.observable.notifyObservers("999");
	}
}