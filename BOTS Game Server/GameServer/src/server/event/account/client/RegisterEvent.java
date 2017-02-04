package server.event.account.client;

import server.account.ServerConnection;
import server.event.ClientEvent;

public class RegisterEvent extends ClientEvent {

	
	public RegisterEvent(ServerConnection connection) {
		super(connection);
	}

	@Override
	public void run() {
		
	}
	
}
