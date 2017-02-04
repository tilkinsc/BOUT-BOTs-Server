package accountserver.event.client;

import accountserver.Account;
import accountserver.Main;
import accountserver.ServerConnection;
import accountserver.UserPack;
import shared.Util;

public class LoginEvent extends ClientEvent {

	
	public LoginEvent(ServerConnection connection) {
		super(connection);
	}
	
	@Override
	public void run() {
		try {
			String user = "", pass = "";
			
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
					pass = line;
					try {
						final UserPack userdata = Account.getUser(user);
						final int login_result = Account.checkUser(userdata, pass);
						
						String status;
						switch (login_result) {
						case 0:
							Account.updateUser(userdata, connection.getSocket().getInetAddress().getHostAddress());
							status = Util.isoString(Account.LOGIN_SUCCESSBYTE);
							break;
						default: // attempt to find out if there is a correct way to give unspecified error
							Main.logger.log("LoginServerConnection", "Unspecified login return");
						case 1:
							status = Util.isoString(Account.LOGIN_INCUSERBYTE);
							break;
						case 2:
							status = Util.isoString(Account.LOGIN_INCPASSBYTE);
							break;
						case 3:
							status = Util.isoString(Account.LOGIN_BANUSERBYTE);
							break;
						case 4:
							status = Util.isoString(Account.LOGIN_ALREADYLOGGEDIN);
							break;
						}
						connection.write(Util.isoString(Account.LOGINHEADER));
						connection.write(status);
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
