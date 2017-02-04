package loginserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.SQLDatabase;
import shared.Util;

public class LoginServerConnection extends Thread {

	protected Socket socket;
	protected BufferedReader socketIn;
	protected PrintWriter socketOut;
	
	public LoginServerConnection(Socket socket) {
		this.socket = socket;
	}
	
	public int checkUser(String user, String pass) {
		System.out.println("Checking user....");
		try {
			final ResultSet rs = SQLDatabase.doquery("SELECT * FROM bout_users WHERE username='" + user + "' LIMIT 1");
			String puser = "", ppass = "";
			int id = 0, banned = 0, allog = 0;
			while (rs.next()) {
				id = rs.getInt("id");
				puser = rs.getString("username");
				ppass = rs.getString("password");
				banned = rs.getInt("banned");
				allog = rs.getInt("online");
			}
			
			Main.logger.log("LoginServerConnection",
					"(" + this.socket.getRemoteSocketAddress() + ")"
						+ id + " " + user + " " + puser + " "
						+ pass + " " + ppass + " " + banned + " "
						+ allog);
			
			if (id == 0)
				return 1;
			else if (!Util.md5hash(pass).equals(ppass))
				return 2;
			else if (banned == 1)
				return 3;
			else if (allog == 1)
				return 4;
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			Main.logger.log("Exception", e.getMessage());
		}
		return 1; // would love to put in unspecified error
	}
	
	protected void doLogin(String user, String pass) {
		System.out.println("Doing login");
		try {
			final int login_result = checkUser(user, pass);
			
			String status;
			switch (login_result) {
			case 0:
				updateaccount(user);
				status = Util.isoString(LoginServer.LOGIN_SUCCESSBYTE);
				break;
			default: // attempt to find out if there is a correct way to give unspecified error
				Main.logger.log("LoginServerConnection", "Unspecified login return");
			case 1:
				status = Util.isoString(LoginServer.LOGIN_INCUSERBYTE);
				break;
			case 2:
				status = Util.isoString(LoginServer.LOGIN_INCPASSBYTE);
				break;
			case 3:
				status = Util.isoString(LoginServer.LOGIN_BANUSERBYTE);
				break;
			case 4:
				status = Util.isoString(LoginServer.LOGIN_ALREADYLOGGEDIN);
				break;
			}
			write(Util.isoString(LoginServer.LOGINHEADER));
			write(status);
			this.socketOut.close();
			Main.logger.log("LoginServerConnection", "Login Sent " + login_result);
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
	private void updateaccount(String user) {
		try {
			int logincount = 0;
			final ResultSet rs = SQLDatabase.doquery("SELECT * FROM bout_users WHERE username='" + user + "' LIMIT 1");
			while (rs.next())
				logincount = rs.getInt("logincount");
			logincount++;
			
			final Date dt = new Date();
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String ip = socket.getInetAddress().getHostAddress();
			
			// add later online=1
			SQLDatabase.doupdate("UPDATE bout_users SET current_ip='" + ip + "', logincount=" + logincount + ", last_ip='"
					+ ip + "', lastlogin='" + df.format(dt) + "' WHERE username='" + user + "'");
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
	public void write(String msg) {
		try {
			this.socketOut.write(msg);
			this.socketOut.flush();
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
	protected String read() {
		final StringBuffer buffer = new StringBuffer();
		try {
			boolean zeroByteRead = false;
			int codePoint;
			while (!zeroByteRead && buffer.length() < 300) {
				codePoint = this.socketIn.read();
				
				if (codePoint == 0)
					zeroByteRead = true;
				else if (Character.isValidCodePoint(codePoint))
					buffer.appendCodePoint(codePoint);
			}
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
		return buffer.toString();
	}
	
	public SocketAddress getRemoteAddress() {
		return this.socket.getRemoteSocketAddress();
	}
	
	@Override
	public void run() {
		try {
			this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.socketOut = new PrintWriter(this.socket.getOutputStream(), true);
			
			String user = "", pass = "";
			
			String line = read();
			while (line != null) {
				if (line.isEmpty()) {
					line = read();
					continue;
				}
				if (line.startsWith("H")) {
					user = line.replace("H", "");
				}
				
				if (line.length() >= 4 && !user.equals(line.replace("H", ""))) {
					pass = line;
					doLogin(user, pass);
					break;
				}
				line = read();
			}
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
		this.finalize();
	}
	
	@Override
	public void finalize() {
		try {
			Main.loginServer.removeClient(this.getRemoteAddress());
			this.socketIn.close();
			this.socketOut.close();
			this.socket.close();
			Main.logger.log("LoginServerConnection", "Thread " + Thread.currentThread() + " client removed");
		} catch (final Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
}