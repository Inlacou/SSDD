package es.deusto.ingenieria.ssdd.chat.data;

import java.util.StringTokenizer;

public class Mensaje {

	private int code;
	private String messageType;
	private String text;
	
	public Mensaje(String s){
		StringTokenizer st = new StringTokenizer(s, " ");
		
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
