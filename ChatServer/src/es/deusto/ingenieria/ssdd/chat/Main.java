package es.deusto.ingenieria.ssdd.chat;

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
			/*
			String a = "John Smith:>:";
			String b = "John Smith"+":>:";
			String c = "John Smith";
			c = c+":>:";
			String d = ":<:" + "John Smith:>:";
			String e = ":<:" + "John Smith"+":>:";
			String f = ":<:" + "John Smith";
			f = f+":>:";
			System.out.println(a);
			System.out.println(b);
			System.out.println(c);
			System.out.println(d);
			System.out.println(e);
			System.out.println(f);
			*/
			
			/*
			String text = "100" + " " + "LIST"+ " " + "000" + " " + ":<:" + "John Smith" + ":>:";
			int i = 0;
			
			while(text.getBytes().length<1024){
				System.out.println(text);
				System.out.println(i);
				text += ":<:" + "John Smith" + ":>:";
				i++;
			}
			*/
			
			Handler h = new Handler(request, reply, buffer, udpSocket);
			h.run();
			
		} catch (SocketException e) {
			System.err.println("# UDPServer Socket error: " + e.getMessage());
		}
		
	}

}
