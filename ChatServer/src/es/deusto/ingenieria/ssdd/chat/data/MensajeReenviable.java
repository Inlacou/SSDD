package es.deusto.ingenieria.ssdd.chat.data;

import java.util.ArrayList;


public class MensajeReenviable extends Mensaje {

	private long time;
	private String ipEnvio;
	private int portEnvio;
	private ArrayList<String> textosRecepcion;
	private String ipRecepcion;
	private int portRecepcion;
	
	public MensajeReenviable(String textoDelMensaje, String ipEnvio, int portEnvio,
			ArrayList<String> textosRecepcion, String ipRecepcion, int portRecepcion, long time) {
		super(textoDelMensaje);
		this.ipEnvio = ipEnvio;
		this.portEnvio = portEnvio;
		this.time = time;
		this.textosRecepcion = textosRecepcion;
		this.ipRecepcion = ipRecepcion;
		this.portRecepcion = portRecepcion;
	}
	
	public MensajeReenviable(String textoDelMensaje, String ipEnvio, int portEnvio,
			String textoRecepcion, String ipRecepcion, int portRecepcion, long time) {
		super(textoDelMensaje);
		this.ipEnvio = ipEnvio;
		this.portEnvio = portEnvio;
		this.time = time;
		this.textosRecepcion = new ArrayList<String>();
		this.textosRecepcion.add(textoRecepcion);
		this.ipRecepcion = ipRecepcion;
		this.portRecepcion = portRecepcion;
	}
	
	public ArrayList<String> getTextosRecepcion() {
		return textosRecepcion;
	}

	public void setTextosRecepcion(ArrayList<String> textosRecepcion) {
		this.textosRecepcion = textosRecepcion;
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

	public int getPortRecepcion() {
		return portRecepcion;
	}

	public void setPortRecepcion(int portRecepcion) {
		this.portRecepcion = portRecepcion;
	}
	
	
	
}
