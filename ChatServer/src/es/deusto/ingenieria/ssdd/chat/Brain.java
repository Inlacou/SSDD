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
		//ka.start();
		
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
			try {
				addUser(m.getText(), ip, port);
				sendMessage("001 INIT OK", ip, port);
			} catch (IPAlreadyInUseException e) {
				sendMessage("004 INIT ERROR IP ALREADY IN USE", ip, port);
			} catch (NickNameAlreadyInUseException e) {
				sendMessage("002 INIT ERROR NICKNAME USED", ip, port);
			} catch (NickNameNotAllowedException e) {
				sendMessage("003 INIT ERROR NICKNAME NOT ALLOWED", ip, port);
			}
			break;
		case 100:
			
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
