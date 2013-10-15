package es.deusto.ingenieria.ssdd.chat;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import es.deusto.ingenieria.ssdd.chat.data.Mensaje;
import es.deusto.ingenieria.ssdd.chat.data.User;
import es.deusto.ingenieria.ssdd.chat.out.KeepAlive;
import es.deusto.ingenieria.ssdd.exceptions.IPAlreadyInUseException;
import es.deusto.ingenieria.ssdd.exceptions.NickNameAlreadyInUseException;
import es.deusto.ingenieria.ssdd.exceptions.NickNameNotAllowedException;

public class Brain {

	Handler handler;
	ArrayList<User> users;

	public Brain(Handler h){
		handler = h;
		users = new ArrayList<User>();
		KeepAlive ka = new KeepAlive(5000, users, h.udpSocket);
		ka.start();
		
		for (int i = 0; i < 100; i++) {
			users.add(new User("nick"+i, "1.1.1."+i, 5959));
		}

		/*
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  @Override
		  public void run() {

		  }
		}, 0, 5, TimeUnit.SECONDS);
		 */
	}

	public void receivedMessage(String string, String ip, int port){
		Mensaje m = new Mensaje(string);
		switch (m.getCode()) {
		case 0:
			//000 INIT nickname
			try {
				addUser(m.getText(), ip, port);
				sendMessage("001 INIT OK", ip, port);
				sendList(ip, port);
			} catch (IPAlreadyInUseException e) {
				sendMessage("004 INIT ERROR IP ALREADY IN USE", ip, port);
			} catch (NickNameAlreadyInUseException e) {
				sendMessage("002 INIT ERROR NICKNAME USED", ip, port);
			} catch (NickNameNotAllowedException e) {
				sendMessage("003 INIT ERROR NICKNAME NOT ALLOWED", ip, port);
			}
			break;
		case 100:
			//100 LIST
			sendList(ip, port);
			break;
		case 103:

			break;
		case 200:

			break;
		case 210:

			break;
		case 213:

			break;
		case 300:

			break;
		case 301:

			break;
		case 400:

			break;

		default:
			break;
		}
	}

	private void sendList(String ip, int port) {
		int code = 100;
		String messageType = "LIST";
		String numeroMensaje;
		Mensaje m;

		for (User u : users) {
			System.out.println(u);
		}

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
			while(i < numeroUsuarios && m.addText(":<:"+(users.get(i).getNick().trim())+":>:")){
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
		users.add(new User(nick, ip, port));
	}

}
