package accountserver.event;

import accountserver.ServerConnection;

public abstract class ServerEvent extends Thread {

	protected final ServerConnection connection;
	
	public ServerEvent(ServerConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public abstract void run();
	
	public ServerConnection getConnection() {
		return this.connection;
	}
	
}
