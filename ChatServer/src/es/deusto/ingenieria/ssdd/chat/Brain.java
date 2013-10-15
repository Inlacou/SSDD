package es.deusto.ingenieria.ssdd.chat;

import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.chat.data.Mensaje;
import es.deusto.ingenieria.ssdd.chat.data.User;
import es.deusto.ingenieria.ssdd.exceptions.IPAlreadyInUseException;
import es.deusto.ingenieria.ssdd.exceptions.NickNameAlreadyInUseException;
import es.deusto.ingenieria.ssdd.exceptions.NickNameNotAllowedException;

public class Brain {

	ArrayList<User> users;
	
	public Brain(){
		users = new ArrayList<User>();
	}
	
	public void receivedMessage(String string, String ip){
		Mensaje m = new Mensaje(string);
		switch (m.getCode()) {
		case 0:
			try {
				addUser(m.getText(), ip);
			} catch (IPAlreadyInUseException e) {
				sendMessage("004 INIT ERROR IP ALREADY IN USE");
			} catch (NickNameAlreadyInUseException e) {
				sendMessage("002 INIT ERROR NICKNAME USED");
			} catch (NickNameNotAllowedException e) {
				sendMessage("003 INIT ERROR NICKNAME NOT ALLOWED");
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
	
	public void sendMessage(String message){
		
	}
	
	public void addUser(String nick, String ip) throws IPAlreadyInUseException, NickNameAlreadyInUseException, NickNameNotAllowedException{
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
	}
	
}
