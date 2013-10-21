package es.deusto.ingenieria.ssdd.chat.data;


public class MensajeReenviable extends Mensaje {

	private long time;
	private String ipEnvio;
	private int portEnvio;
	
	public MensajeReenviable(String s, long time, String ipEnvio, int portEnvio){
		super(s);
		this.time = time;
		this.ipEnvio = ipEnvio;
		this.portEnvio = portEnvio;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getIpEnvio() {
		return ipEnvio;
	}

	public void setIpEnvio(String ipEnvio) {
		this.ipEnvio = ipEnvio;
	}

	public int getPortEnvio() {
		return portEnvio;
	}

	public void setPortEnvio(int portEnvio) {
		this.portEnvio = portEnvio;
	}
	
	
	
}
