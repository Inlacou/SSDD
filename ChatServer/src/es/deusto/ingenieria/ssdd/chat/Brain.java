package es.deusto.ingenieria.ssdd.chat;

import es.deusto.ingenieria.ssdd.chat.data.Mensaje;

public class Brain {

	public Brain(){
		
	}
	
	public void receivedMessage(String string){
		Mensaje m = new Mensaje(string);
	}
	
	public void sendMessage(String message){
		
	}

	
	
	
}
