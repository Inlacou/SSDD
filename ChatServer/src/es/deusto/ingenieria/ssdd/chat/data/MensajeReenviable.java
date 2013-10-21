package es.deusto.ingenieria.ssdd.chat.data;


public class MensajeReenviable extends Mensaje {

	private long time;
	private String ipEnvio;
	private int portEnvio;
	private String mensajeRecepcion;
	private String ipRecepcion;
	
	public MensajeReenviable(String textoDelMensaje, String ipEnvio, int portEnvio,
			String mensajeRecepcion, String ipRecepcion, long time) {
		super(textoDelMensaje);
		this.ipEnvio = ipEnvio;
		this.portEnvio = portEnvio;
		this.time = time;
		this.mensajeRecepcion = mensajeRecepcion;
		this.ipRecepcion = ipRecepcion;
	}
	
	public String getMensajeRecepcion() {
		return mensajeRecepcion;
	}

	public void setMensajeRecepcion(String mensajeRecepcion) {
		this.mensajeRecepcion = mensajeRecepcion;
	}

	public String getIpRecepcion() {
		return ipRecepcion;
	}

	public void setIpRecepcion(String ipRecepcion) {
		this.ipRecepcion = ipRecepcion;
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
