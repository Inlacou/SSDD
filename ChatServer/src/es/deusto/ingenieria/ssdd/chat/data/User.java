package es.deusto.ingenieria.ssdd.chat.data;

public class User {	
	private String nick;
	private String ip;
	private String port;
	
	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getIP() {
		return ip;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public String getPort(){
		return port;
	}
	
	public void setPort(String port){
		this.port = port;
	}
		
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {			
			return this.nick.equalsIgnoreCase(((User)obj).nick);				  
		} else {
			return false;
		}
	}
}