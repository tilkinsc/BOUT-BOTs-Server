package accountserver.event;

import accountserver.ServerConnection;

public class RegisterEvent extends ClientPath {

	public RegisterEvent(ServerConnection connection) {
		super(connection);
	}

	@Override
	public void run() {
		
	}
	
}
