package accountserver.event.client;

import accountserver.ServerConnection;

public class RegisterEvent extends ClientEvent {

	
	public RegisterEvent(ServerConnection connection) {
		super(connection);
	}

	@Override
	public void run() {
		
	}
	
}
