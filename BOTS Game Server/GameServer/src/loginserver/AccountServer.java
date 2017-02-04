package loginserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import loginserver.event.LoginEvent;
import loginserver.event.RegisterEvent;
import shared.SQLDatabase;
import shared.Util;


public class AccountServer extends Thread {

	public static final byte[] LOGIN_REQUEST = {(byte) 0xF8, (byte) 0x2A, (byte) 0x40};
	public static final byte[] REGISTER_REQUEST = {(byte) 0xF9, (byte) 0x2A, (byte) 0x40};
	public static final String LOGIN_REQUEST_STR = Util.isoString(LOGIN_REQUEST);
	public static final String REGISTER_REQUEST_STR = Util.isoString(REGISTER_REQUEST);
	
	public static final byte[] LOGIN_SUCCESSBYTE = {(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] LOGIN_INCUSERBYTE = {(byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] LOGIN_INCPASSBYTE = {(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] LOGIN_BANUSERBYTE = {(byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] LOGIN_ALREADYLOGGEDIN = {(byte) 0x01, (byte) 0x00, (byte) 0x06, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] LOGINHEADER = {(byte) 0xEC, (byte) 0x2C, (byte) 0x4A, (byte) 0x00};
	
	protected ServerSocket socketServer;
	protected Vector<ServerConnection> clientConnections;
	
	public final int port;
	public final int timeout;
	
	public AccountServer(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
		this.clientConnections = new Vector<ServerConnection>();
	}
	
	public static UserPack getUser(String user) throws SQLException {
		final ResultSet rs = SQLDatabase.doquery("SELECT * FROM bout_users WHERE username='" + user + "' LIMIT 1");
		while (rs.next()) {
			return new UserPack(
					rs.getString("username"),
					rs.getString("password"),
					rs.getInt("id"),
					rs.getInt("banned"),
					rs.getInt("online"),
					rs.getInt("logincount")
				);
		}
		return null;
	}
	
	public static int checkUser(UserPack userdata, String pass) {
		if (userdata.getId() == 0)
			return 1;
		else if (!Util.md5hash(pass).equals(userdata.getPass()))
			return 2;
		else if (userdata.getBanned() == 1)
			return 3;
		else if (userdata.isAlreadyLogged() == 1)
			return 4;
		return 0;
	}
	
	public static void updateUser(UserPack userdata, String ip) {
		try {
			final Date dt = new Date();
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// add later online=1
			SQLDatabase.doupdate("UPDATE bout_users SET current_ip='" + ip + "', logincount=" + (userdata.getLoginCount()+1) + ", last_ip='"
					+ ip + "', lastlogin='" + df.format(dt) + "' WHERE username='" + userdata.getUser() + "'");
		} catch (Exception e) {
			Main.logger.log("Error", e.getMessage());
		}
	}
	
	private boolean stop;
	
	@Override
	public void start() {
		stop = false;
		super.start();
	}
	
	@Override
	public void run() {
		try {
			Main.logger.log("LoginServer", "Has Hopped on " + this.port + "!");
			this.socketServer = new ServerSocket(port);
			this.socketServer.setSoTimeout(timeout);
			
			while (!stop) {
				try {
					final Socket socket = this.socketServer.accept();
					
					Main.logger.log("LoginServer", "Client connection from " + socket.getInetAddress().getHostAddress());
					final ServerConnection socketConnection = new ServerConnection(socket);
					final String header = socketConnection.read();
					if (LOGIN_REQUEST_STR.equals(header)) {
						new LoginEvent(socketConnection).start();
					} else if (REGISTER_REQUEST_STR.equals(header)) {
						new RegisterEvent(socketConnection).start();
					}
					this.clientConnections.add(socketConnection);
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
			
			removeAllClients();
			this.socketServer.close();
			Main.logger.log("LoginServer", "Stopped login server.");
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
	public void stopThread() {
		this.stop = true;
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeClient(SocketAddress remoteAddress) {
		try {
			for (int i = 0; i < this.clientConnections.size(); i++) {
				final ServerConnection con = this.clientConnections.get(i);
				if (con.getRemoteAddress().equals(remoteAddress)) {
					this.clientConnections.remove(i);
					con.finalize();
					Main.logger.log("LoginServer", remoteAddress + " removed");
					return true;
				}
			}
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
		return false;
	}
	
	public void removeAllClients() {
		try {
			for (int i=0; i<this.clientConnections.size(); i++)
				this.clientConnections.get(i).finalize();
			this.clientConnections.clear();
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
	public int getPort() {
		return this.port;
	}
	
	public int getClientCount() {
		return this.clientConnections.size();
	}
	
}
