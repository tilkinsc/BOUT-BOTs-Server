package accountserver.event;

import accountserver.ServerConnection;

public abstract class ClientEvent extends Thread {

	protected final ServerConnection connection;
	
	public ClientEvent(ServerConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public abstract void run();
	
	public ServerConnection getConnection() {
		return this.connection;
	}
	
}
