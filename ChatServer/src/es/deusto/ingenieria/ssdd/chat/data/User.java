package es.deusto.ingenieria.ssdd.chat.data;

public class User {	
	private String nick;
	private String ip;
	private int port;
	
	public User(String nick, String ip, int port){
		this.nick = nick;
		this.ip = ip;
		this.port = port;
	}
	
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
	
	public int getPort(){
		return port;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	@Override
	public String toString() {
		return "User.toString() | Nickname: "+nick+" IP: "+ip+" Port: "+port;
	}
		
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {			
			return this.nick.equalsIgnoreCase(((User)obj).nick);				  
		} else {
			return false;
		}
	}
}