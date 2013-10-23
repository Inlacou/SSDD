package es.deusto.ingenieria.ssdd.chat.keepalive;

import java.util.GregorianCalendar;

import es.deusto.ingenieria.ssdd.chat.client.controller.ClientThread;

public class KeepAlive extends Thread {
	
	private ClientThread ct;
	private long lastKeepAlive;
	private long timeToWaitLong;
	private boolean stop;
	
	public KeepAlive(ClientThread ct) {
		this.ct = ct;
		this.lastKeepAlive = 0;
		this.timeToWaitLong = 120000;
		this.stop = false;
	}

	@Override
	public void run() {
		
		while (!stop) {
			
			try {
				Thread.sleep(130000);
				compareLastKeepAlive();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setLastKeepAlive() {
		this.lastKeepAlive = GregorianCalendar.getInstance().getTime().getTime();
	}
	
	public void compareLastKeepAlive() {
		
		long currentTime = GregorianCalendar.getInstance().getTime().getTime();
		
		if ((currentTime - lastKeepAlive) > timeToWaitLong) {
			
			stopThread();
			ct.stopThread();
		}
	}
	
	public void stopThread() {
		this.stop = true;
	}
}
