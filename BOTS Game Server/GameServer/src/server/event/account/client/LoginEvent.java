package server.event.account.client;

import server.Main;
import server.account.Account;
import server.account.ServerConnection;
import server.account.UserPack;
import server.event.ClientEvent;
import shared.Packet;
import shared.Util;

public class LoginEvent extends ClientEvent {

	
	public LoginEvent(ServerConnection connection) {
		super(connection);
	}
	
	@Override
	public void run() {
		try {
			String user = "";
			
			String line;
			while ((line = connection.read()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("H")) {
					user = line.replace("H", "");
					continue;
				}
				if (line.length() >= 4 && !user.equals(line.replace("H", ""))) {
					try {
						final UserPack userdata = Account.getUser(user);
						final int login_result = Account.checkUser(userdata, line);
						
						final Packet packet = new Packet();
						packet.setHead(Util.isoString(Account.LOGINHEADER));
						
						switch (login_result) {
						case 0:
							Account.updateUser(userdata, connection.getSocket().getInetAddress().getHostAddress());
							packet.setBody(Util.isoString(Account.LOGIN_SUCCESSBYTE));
							break;
						default: // attempt to find out if there is a correct way to give unspecified error
							Main.logger.log("LoginServerConnection", "Unspecified login return");
						case 1:
							packet.setBody(Util.isoString(Account.LOGIN_INCUSERBYTE));
							break;
						case 2:
							packet.setBody(Util.isoString(Account.LOGIN_INCPASSBYTE));
							break;
						case 3:
							packet.setBody(Util.isoString(Account.LOGIN_BANUSERBYTE));
							break;
						case 4:
							packet.setBody(Util.isoString(Account.LOGIN_ALREADYLOGGEDIN));
							break;
						}
						connection.write(packet);
						connection.getSocket().close();
						Main.logger.log("LoginServerConnection", "Login Sent " + login_result);
					} catch (Exception e) {
						Main.logger.log("Error", e.getMessage());
					}
					break;
				}
			}
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
		connection.finalize();
	}

}
