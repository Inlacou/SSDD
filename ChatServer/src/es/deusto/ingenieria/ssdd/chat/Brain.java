package es.deusto.ingenieria.ssdd.chat;

import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.Chat;
import es.deusto.ingenieria.ssdd.chat.data.Mensaje;
import es.deusto.ingenieria.ssdd.chat.data.User;
import es.deusto.ingenieria.ssdd.chat.out.KeepAlive;
import es.deusto.ingenieria.ssdd.exceptions.IPAlreadyInUseException;
import es.deusto.ingenieria.ssdd.exceptions.NickNameAlreadyInUseException;
import es.deusto.ingenieria.ssdd.exceptions.NickNameNotAllowedException;
import es.deusto.ingenieria.ssdd.exceptions.NotLoggedInException;

//TODO puertos

public class Brain {

	Handler handler;
	ArrayList<User> users;
	ArrayList<Chat> chats;
	Brain2 brain2;

	public Brain(Handler h){
		handler = h;
		users = new ArrayList<User>();
		KeepAlive ka = new KeepAlive(300000, users, h.udpSocket);
		ka.start();
		brain2 = new Brain2(h.udpSocket, this, h);
		brain2.start();
		for (int i = 0; i < 1; i++) {
			users.add(new User("nick"+i, "1.1.1."+i, 8000+i));
		}
	}

	public void receivedMessage(String string, String ip, int messagePort){
		User u = null;
		int numeroUsuarios = users.size();
		for (int i = 0; i < numeroUsuarios; i++) {
			u = users.get(i);
			if(u.getIP().equals(ip)){
				break;
			}
		}
		Mensaje m = new Mensaje(string);
		switch (m.getCode()) {
		case 0:
			//received 000 INIT nickname
			try {
				addUser(m.getText(), ip, messagePort);
				//sendList(ip, messagePort);
				sendMessage("001 INIT OK "+messagePort, ip, messagePort);
			} catch (IPAlreadyInUseException e) {
				sendMessage("004 INIT ERROR IP ALREADY IN USE", ip, messagePort);
			} catch (NickNameAlreadyInUseException e) {
				sendMessage("002 INIT ERROR NICKNAME USED", ip, messagePort);
			} catch (NickNameNotAllowedException e) {
				sendMessage("003 INIT ERROR NICKNAME NOT ALLOWED", ip, messagePort);
			}
			break;
		case 100:
			//received 100 LIST
			sendList(ip, u.getPort());
			break;
		case 104:
			//received 104 LISTERROR last_XXX_without_blanks
			sendList(ip, messagePort, m.getText());
			break;
		case 200:
			//received 200 INITCHAT user
			try {
				userLoggedIn(ip);
			} catch (NotLoggedInException e) {
				sendMessage("666 ERROR NOT LOGGED IN", ip, u.getPort());
			}
			try{
				initChat(m.getText(), ip, messagePort);
			}catch (NullPointerException e) {
				sendMessage("204 CHAT ERROR USER DOES NOT EXIST", ip, messagePort);
			}
			break;
		case 210:
			//received 210 SENDMSG text
			try {
				userLoggedIn(ip);
			} catch (NotLoggedInException e) {
				sendMessage("666 ERROR NOT LOGGED IN", ip, u.getPort());
			}
			enviarMensaje(m.getText(), ip);
			break;
		case 300:
			//300 LEAVECHAT
			try {
				userLoggedIn(ip);
			} catch (NotLoggedInException e) {
				sendMessage("666 ERROR NOT LOGGED IN", ip, u.getPort());
			}
			sendMessage("301 LEAVECHAT OK", ip, messagePort);
			leaveChat(ip, messagePort);
			break;
		case 301:
			//301 LEAVECHAT OK
			brain2.receivedMessage(string, ip);
			try {
				userLoggedIn(ip);
			} catch (NotLoggedInException e) {
				sendMessage("666 ERROR NOT LOGGED IN", ip, u.getPort());
			}
			break;
		case 400:
			//400 LEAVEAPP
			try {
				userLoggedIn(ip);
			} catch (NotLoggedInException e) {
				sendMessage("666 ERROR NOT LOGGED IN", ip, u.getPort());
			}
			leaveApp(m.getText(), ip, messagePort);
			break;
		default:
			break;
		}
	}

	private void enviarMensaje(String text, String ip) {
		Chat c = null;
		User u = null;
		int numeroChats = chats.size();
		
		for (int i = 0; i < numeroChats; i++) {
			c = chats.get(i);
			if(c.getUser1().getIP().equals(ip)){
				u = c.getUser2();
			}else if(c.getUser2().getIP().equals(ip)){
				u = c.getUser1();
			}
		}
		
		sendMessage(text, u.getIP(), u.getPort());
		
	}

	private void leaveChat(String ip, int port) {
		User u = null;
		Chat c;
		int numeroChats = chats.size();
		for (int i = 0; i < numeroChats; i++) {
			c = chats.get(i);
			if(c.getUser1().getIP().equals(ip)){
				u = c.getUser2();
				break;
			}else if(c.getUser2().getIP().equals(ip)){
				u = c.getUser1();
				break;
			}
		}
		sendMessage("300 LEAVECHAT", u.getIP(), u.getPort());
		brain2.registerMessage("300 LEAVECHAT", u.getIP(), u.getPort(), "301 LEAVECHAT OK", ip, port);
	}

	public void addChat(String ip1, String ip2){
		User u1 = null;
		User u2 = null;
		User uAux;
		int numeroUsuarios = users.size();
		for (int i = 0; i < numeroUsuarios; i++) {
			uAux = users.get(i);
			if(uAux.getIP().equals(ip1)){
				u1 = uAux;
			}else if(uAux.getIP().equals(ip2)){
				u1 = uAux;
			}
		}
		chats.add(new Chat(u1, u2));
	}

	private void initChat(String text, String ip, int port){
		User destinationUser = null;
		int numeroUsuarios = users.size();
		for (int i = 0; i < numeroUsuarios; i++) {
			if(users.get(i).getNick().trim().equals(text.trim())){
				destinationUser = users.get(i);
			}
		}
		sendMessage("200 INITCHAT "+text, destinationUser.getIP(), destinationUser.getPort());
		sendMessage("201 CHAT OK", ip, port);
		brain2.registerMessage("200 INITCHAT "+text, destinationUser.getIP(), destinationUser.getPort(), "202 CHAT ACCEPTED", "202 CHAT ACCEPTED", ip, port);
	}

	private void userLoggedIn(String ip) throws NotLoggedInException {
		int numeroUsuarios = users.size();
		User u;
		String leftUserNickName = "";
		for (int i = 0; i < numeroUsuarios; i++) {
			u = users.get(i);
			if(u.getIP().equals(ip)){
				leftUserNickName = users.get(i).getNick();
			}
		}
		if(leftUserNickName.equals("")){
			throw new NotLoggedInException();
		}
	}

	private void leaveApp(String text, String ip, int port) {
		int numeroUsuarios = users.size();
		User u = null;
		String leftUserNickName = "";
		for (int i = 0; i < numeroUsuarios; i++) {
			u = users.get(i);
			if(u.getIP().equals(ip)){
				leftUserNickName = users.get(i).getNick();
			}
		}
		if(!leftUserNickName.equals("")){
			for (int i = 0; i < numeroUsuarios; i++) {
				u = users.get(i);
				sendMessage("103 LEFTUSER " + leftUserNickName, u.getIP(), u.getPort());
			}
			users.remove(u);
			sendMessage("401 LEAVEAPP OK", ip, port);
		}
	}

	private void sendList(String ip, int port, String text) {
		int code = 100;
		String messageType = "LIST";
		String numeroMensaje;
		Mensaje m;

		int numeroUsuarios = users.size();
		int numeroMensajes = 0;
		int numeroMensajesYaEnviados = Integer.parseInt(text);

		for (int i = 0; i < numeroUsuarios; i++) {
			m = new Mensaje();
			m.setCode(code);
			numeroMensaje = Integer.toString(numeroMensajes);
			while(numeroMensaje.length()<3){
				numeroMensaje = "0"+numeroMensaje;
			}
			m.setMessageType(messageType);
			m.setText(numeroMensaje+" ");
			while(i < numeroUsuarios && m.addText(":<:"+(users.get(i).getNick())+":>:")){
				i++;
			}
			numeroMensajes++;
			if(numeroMensajes>numeroMensajesYaEnviados){
				sendMessage(m, ip, port);
			}
		}
	}

	private void sendList(String ip, int port) {
		int code = 100;
		String messageType = "LIST";
		String numeroMensaje;
		Mensaje m;

		int numeroUsuarios = users.size();
		int numeroMensajes = 0;

		for (int i = 0; i < numeroUsuarios; i++) {
			m = new Mensaje();
			m.setCode(code);
			numeroMensaje = Integer.toString(numeroMensajes);
			while(numeroMensaje.length()<3){
				numeroMensaje = "0"+numeroMensaje;
			}
			m.setMessageType(messageType);
			m.setText(numeroMensaje+" ");
			while(i < numeroUsuarios && m.addText(":<:"+(users.get(i).getNick())+":>:")){
				i++;
			}
			numeroMensajes++;
			sendMessage(m, ip, port);
		}

	}

	private void sendMessage(Mensaje m, String ip, int port) {
		handler.sendMessage(m.getCode()+" "+m.getMessageType()+" "+m.getText(), ip, port);
	}

	public void sendMessage(String message, String ip, int port){
		handler.sendMessage(message, ip, port);
	}

	public void addUser(String nick, String ip, int port) throws IPAlreadyInUseException, NickNameAlreadyInUseException, NickNameNotAllowedException{
		User auxUser;
		int numeroUsuarios = users.size();
		for (int i = 0; i < numeroUsuarios; i++) {
			auxUser = users.get(i);
			if(auxUser.getIP().equals(ip)){
				throw new IPAlreadyInUseException();
			}else if(auxUser.getNick().equals(nick)){
				throw new NickNameAlreadyInUseException();
			}else if(nick.contains(":<:") && nick.contains(":>:")){
				throw new NickNameNotAllowedException();
			}
		}
		for (int i = 0; i < numeroUsuarios; i++) {
			auxUser = users.get(i);
			sendMessage("102 NEWUSER "+nick, auxUser.getIP(), auxUser.getPort());
		}
		users.add(new User(nick, ip, port));
	}

}
