package server.account.event.client;

import server.account.ServerConnection;
import server.account.event.ClientEvent;

public class RegisterEvent extends ClientEvent {

	
	public RegisterEvent(ServerConnection connection) {
		super(connection);
	}

	@Override
	public void run() {
		
	}
	
}
