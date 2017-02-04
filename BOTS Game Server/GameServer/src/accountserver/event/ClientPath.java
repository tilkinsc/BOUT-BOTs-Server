package accountserver.event;

import accountserver.ServerConnection;

public abstract class ClientPath extends Thread {

	protected final ServerConnection connection;
	
	public ClientPath(ServerConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public abstract void run();
	
	public ServerConnection getConnection() {
		return this.connection;
	}
	
}
