package accountserver.event.server;

public abstract class ServerPath extends Thread {

	protected final int port, timeout;
	protected boolean stop;
	
	public ServerPath(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
	}
	
	@Override
	public void start() {
		this.stop = false;
		super.start();
	}
	
	@Override
	public abstract void run();
	
	public void stopThread() {
		this.stop = true;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public int getSOTimeout() {
		return this.timeout;
	}
	
	public boolean isStopped() {
		return this.stop;
	}
	
}
