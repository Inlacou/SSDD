package es.deusto.ingenieria.ssdd.chat.data;

import java.util.ArrayList;

public class Chat {

	User founder;
	User receiver;
	ArrayList<Mensaje> mensajes;
	
	public Chat(User founder, User receiver){
		this.founder = founder;
		this.receiver = receiver;
		mensajes = new ArrayList<Mensaje>();
	}
	
	public void nuevoMensaje(Mensaje m){
		mensajes.add(m);
	}
	
	public User getUser1(){
		return founder;
	}
	
	public User getUser2(){
		return receiver;
	}
	
}
