package loginserver.event;

import loginserver.ServerConnection;

public class RegisterEvent extends ServerEvent {

	public RegisterEvent(ServerConnection connection) {
		super(connection);
	}

	@Override
	public void run() {
		
	}
	
}
