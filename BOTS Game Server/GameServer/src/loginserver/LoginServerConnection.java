package loginserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.Packet;
import shared.SQLDatabase;
import shared.Util;

public class LoginServerConnection extends Thread {

	protected Socket socket;
	protected BufferedReader socketIn;
	protected PrintWriter socketOut;
	
	public String user;
	public String pass;
	
	// unused
	// public int LOGIN_ID;
	// public String LOGIN_USERNAME;
	// public String LOGIN_PASSWORD;
	// public int LOGIN_BANNED;
	// public int LOGIN_ALLOG;
	// public int LOGIN_RESULT;
	// public String LOGIN_RESULTSTR;
	
	// unused
	//private String pass1;
	//private String oldline;
	
	public LoginServerConnection(Socket socket) {
		this.socket = socket;
	}
	
	public SocketAddress getRemoteAddress() {
		return this.socket.getRemoteSocketAddress();
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
			else if (!Util.md5hash(this.pass).equals(ppass))
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
	
	protected void doLogin() {
		System.out.println("Doing login");
		try {
			final int login_result = checkUser(this.user, this.pass);
			
			final Packet packet = new Packet();
			packet.setHead(Util.isoString(LoginServer.LOGINHEADER));
			
			switch (login_result) {
			case 0:
				updateaccount(user);
				packet.setBody(Util.isoString(LoginServer.LOGIN_SUCCESSBYTE));
				break;
			default: // attempt to find out if there is a correct way to give unspecified error
				Main.logger.log("LoginServerConnection", "Unspecified login return");
			case 1:
				packet.setBody(Util.isoString(LoginServer.LOGIN_INCUSERBYTE));
				break;
			case 2:
				packet.setBody(Util.isoString(LoginServer.LOGIN_INCPASSBYTE));
				break;
			case 3:
				packet.setBody(Util.isoString(LoginServer.LOGIN_BANUSERBYTE));
				break;
			case 4:
				packet.setBody(Util.isoString(LoginServer.LOGIN_ALREADYLOGGEDIN));
				break;
			}
			this.socketOut.write(packet.getHead());
			this.socketOut.flush();
			this.socketOut.write(packet.getBody());
			this.socketOut.flush();
			this.socketOut.close();
			Main.logger.log("LoginServerConnection", "Response sent " + login_result);
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
	private void updateaccount(String user) {
		try {
			int logincount = 0;
			String old_ip = "";
			final ResultSet rs = SQLDatabase.doquery("SELECT * FROM bout_users WHERE username='" + user + "' LIMIT 1");
			while (rs.next()) {
				logincount = rs.getInt("logincount");
				old_ip = rs.getString("last_ip");
			}
			logincount++;
			
			final Date dt = new Date();
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String ip = socket.getInetAddress().getHostAddress();
			
			// add later online=1
			SQLDatabase.doupdate("UPDATE bout_users SET current_ip='" + ip + "', logincount=" + logincount + ", last_ip='"
					+ old_ip + "', lastlogin='" + df.format(dt) + "' WHERE username='" + user + "'");
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
	public void write(String msg) {
		try {
			this.socketOut.write(msg + "\u0000");
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
	
	@Override
	public void run() {
		try {
			this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.socketOut = new PrintWriter(this.socket.getOutputStream(), true);
			
			String line = read();
			while (line != null) {
				if (!line.isEmpty()) {
					if (line.startsWith("H")) {
						final String newLine = line.replace("H", "");
						this.user = newLine;
					}
					
					if (line.length() >= 4 && !this.user.equals(line.replace("H", ""))) {
						this.pass = line;
						doLogin();
						break;
					}
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