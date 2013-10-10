package es.deusto.ingenieria.ssdd.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {
	
	private static final int PORT = 6789;
	
	public static void main(String[] args) {
		int serverPort = args.length == 0 ? Main.PORT : Integer.parseInt(args[0]);
		
		try (DatagramSocket udpSocket = new DatagramSocket(serverPort)) {
			DatagramPacket request = null;
			DatagramPacket reply = null;
			byte[] buffer = new byte[1024];
			
			System.out.println(" - Waiting for connections '" + 
			                       udpSocket.getLocalAddress().getHostAddress() + ":" + 
					               serverPort + "' ...");
			Handler h = new Handler(request, reply, buffer, udpSocket);
			h.run();
			
		} catch (SocketException e) {
			System.err.println("# UDPServer Socket error: " + e.getMessage());
		}
		
	}

}
