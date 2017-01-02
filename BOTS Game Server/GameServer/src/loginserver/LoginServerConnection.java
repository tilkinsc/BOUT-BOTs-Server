/*
 * LoginServerConnection.java
 * This file handles the connections.
 */

package loginserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
//import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.util.*;
import java.sql.ResultSet;

/**
 * The LoginServerConnection handles individual client connections to the chat
 * server.
 */
public class LoginServerConnection extends Thread {

	protected Socket socket;
	protected BufferedReader socketIn;
	protected PrintWriter socketOut;
	
	public String user;
	public String pass;
	
	public int LOGIN_ID;
	public String LOGIN_USERNAME;
	public String LOGIN_PASSWORD;
	public int LOGIN_BANNED;
	public int LOGIN_ALLOG;
	public int LOGIN_RESULT;
	public String LOGIN_RESULTSTR;
	
	// unused
	//private String pass1;
	//private String oldline;

	/**
	 * Creates a new instance of LoginServerConnection.
	 */
	public LoginServerConnection(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Gets the remote address of the client.
	 */
	public SocketAddress getRemoteAddress() {
		return this.socket.getRemoteSocketAddress();
	}

	public void CheckUser(String user, String pass) {
		System.out.println("Checking user....");
		try {
			final ResultSet rs = Main.sql.doquery("SELECT * FROM bout_users WHERE username='" + user + "' LIMIT 1");
			while (rs.next()) {
				this.LOGIN_ID = rs.getInt("id");
				this.LOGIN_USERNAME = rs.getString("username");
				this.LOGIN_PASSWORD = rs.getString("password");
				this.LOGIN_BANNED = rs.getInt("banned");
				this.LOGIN_ALLOG = rs.getInt("online");
				this.LOGIN_RESULT = 0;

			}
			if (this.LOGIN_ID == 0)
				this.LOGIN_RESULT = 1;
			else if (!md5hash(this.pass).equals(this.LOGIN_PASSWORD))
				this.LOGIN_RESULT = 2;
			else if (this.LOGIN_BANNED == 1)
				this.LOGIN_RESULT = 3;
			else if (this.LOGIN_ALLOG == 1)
				this.LOGIN_RESULT = 4;
			Main.logger.log("LoginServerConnection",
				"(" + this.socket.getRemoteSocketAddress() + ")"
					+ LOGIN_ID + " " + LOGIN_USERNAME + " "
					+ LOGIN_PASSWORD + " " + LOGIN_BANNED + " "
					+ LOGIN_ALLOG + " " + LOGIN_RESULT);
		} catch (Exception e) {
			e.printStackTrace();
			Main.logger.log("Exception", e.getMessage());
		}
	}

	/**
	 * Writes the login packet.
	 */
	protected void doLogin() {
		System.out.println("Doing login");
		try {
			CheckUser(this.user, this.pass);
			switch (this.LOGIN_RESULT) {

			case 0:
				updateaccount(user);
				this.socketOut.write(new String(LoginServer.LOGINHEADER, "ISO8859-1"));
				this.socketOut.flush();
				this.socketOut.write(new String(LoginServer.LOGIN_SUCCESSBYTE, "ISO8859-1"));
				this.socketOut.flush();
				this.LOGIN_RESULTSTR = "Success";
				this.socket.close();
				break;
			case 1:
			default:
				this.socketOut.write(new String(LoginServer.LOGINHEADER, "ISO8859-1"));
				this.socketOut.flush();
				this.socketOut.write(new String(LoginServer.LOGIN_INCUSERBYTE, "ISO8859-1"));
				this.socketOut.flush();
				this.LOGIN_RESULTSTR = "Incorrect Username";
				break;
			case 2:
				this.socketOut.write(new String(LoginServer.LOGINHEADER, "ISO8859-1"));
				this.socketOut.flush();
				this.socketOut.write(new String(LoginServer.LOGIN_INCPASSBYTE, "ISO8859-1"));
				this.socketOut.flush();
				this.LOGIN_RESULTSTR = "Incorrect Password";
				break;
			case 3:
				this.socketOut.write(new String(LoginServer.LOGINHEADER, "ISO8859-1"));
				this.socketOut.flush();
				this.socketOut.write(new String(LoginServer.LOGIN_BANUSERBYTE, "ISO8859-1"));
				this.socketOut.flush();
				this.LOGIN_RESULTSTR = "Banned Username";
				break;
			case 4:
				this.socketOut.write(new String(LoginServer.LOGINHEADER, "ISO8859-1"));
				this.socketOut.flush();
				this.socketOut.write(new String(LoginServer.LOGIN_ALREADYLOGGEDIN, "ISO8859-1"));
				this.socketOut.flush();
				this.LOGIN_RESULTSTR = "User is already Logged in";
				break;
			}
			Main.logger.log("LoginServerConnection", "Login Sent " + this.LOGIN_RESULTSTR);
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}

	/**
	 * update account information
	 */
	private void updateaccount(String user) {
		try {
			int logincount = 0;
			final ResultSet rs = Main.sql.doquery("SELECT * FROM bout_users WHERE username='" + user + "' LIMIT 1");
			while (rs.next())
				logincount = rs.getInt("logincount");
			logincount++;

			// get date
			final java.util.Date dt = new java.util.Date();
			// set date format
			final java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String ip = Main.getip(socket);

			// add later
			// online=1
			Main.sql.doupdate("UPDATE bout_users SET current_ip='" + ip + "', logincount=" + logincount + ", last_ip='"
					+ ip + "', lastlogin='" + df.format(dt) + "' WHERE username='" + user + "'");
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}

	/**
	 * Sends a message to the connected party.
	 *
	 */
	public void write(String msg) {
		try {
			this.socketOut.write(msg + "\u0000");
			this.socketOut.flush();
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}

	/**
	 * md5hash fuction for checkuser (database password hashing)
	 */

	private String md5hash(String text) {
		try {
			MessageDigest md = null;
			byte[] encryptMsg = null;
			try {
				md = MessageDigest.getInstance("MD5");
				encryptMsg = md.digest(text.getBytes("ISO8859-1"));
			} catch (NoSuchAlgorithmException e) {
			}
			String swap = "";
			String byteStr = "";
			final StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i <= encryptMsg.length - 1; i++) {
				byteStr = Integer.toHexString(encryptMsg[i]);
				switch (byteStr.length()) {
				case 1:
					swap = "0" + Integer.toHexString(encryptMsg[i]);
					break;
				case 2:
					swap = Integer.toHexString(encryptMsg[i]);
					break;
				case 8:
					swap = (Integer.toHexString(encryptMsg[i])).substring(6, 8);
					break;
				}
				strBuf.append(swap);
			}
			final String hash = strBuf.toString();
			return hash;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Reads the buffer.
	 *
	 */
	protected String read() {
		final StringBuffer buffer = new StringBuffer();
		int codePoint;
		boolean zeroByteRead = false;

		try {
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

	/**
	 * Waits from messages from the client...
	 */
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

	/**
	 * Closes the reader, the writer and the socket.
	 */
	@Override
	protected void finalize() {
		try {
			Main.loginServer.remove(this.getRemoteAddress());
			this.socketIn.close();
			this.socketOut.close();
			this.socket.close();
			Main.logger.log("LoginServerConnection", "Thread " + Thread.currentThread() + " client removed");
		} catch (final Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}

}