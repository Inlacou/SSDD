package es.deusto.ingenieria.ssdd.chat.data;

import java.util.StringTokenizer;

public class Mensaje {

	private int code;
	private String messageType;
	private String text;
	
	public Mensaje(String s){
		StringTokenizer st = new StringTokenizer(s, " ");
		this.code = Integer.parseInt(st.nextToken());
		this.messageType = st.nextToken();
		text = "";
		while (st.hasMoreElements()) {
			text += st.nextToken() + " ";
		}
		text = text.trim();
	}
	
	public Mensaje() {
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
	
	@Override
	public String toString() {
		return "Mensaje.toString() | Code: "+code+" MessageType: "+messageType+" Text: "+text;
	}
	
	public static void main(String [] args){
		String mensaje1 = "000 INIT John Smith";
		
		Mensaje m1 = new Mensaje(mensaje1);
		
		System.out.println(m1);
		
	}

	public boolean addText(String string) {
		String auxText = text+string;
		if((Integer.toString(code)+" "+messageType+" "+auxText).getBytes().length>1024){
			return false;
		}
		text = auxText;
		return true;
	}

}
