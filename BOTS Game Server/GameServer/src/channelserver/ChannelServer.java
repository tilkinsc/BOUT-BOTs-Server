package channelserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Vector;

import accountserver.Main;
import shared.Util;

public class ChannelServer extends Thread {

	public static final byte[] PACKETS_HEADER = { (byte) 0x01, (byte) 0x00 };
	
	public static final byte[] BOT_CREATION_HEADER = { (byte) 0xE2, (byte) 0x2E, (byte) 0x02, (byte) 0x00 };
	public static final byte[] CREATE_BOT_USERNAME_TAKEN = { (byte) 0x00, (byte) 0x36 };
	public static final byte[] CREATE_BOT_USERNAME_ERROR = { (byte) 0x00, (byte) 0x33 };
	public static final byte[] CREATE_BOT_CREATED = { (byte) 0x01, (byte) 0x00 };
	
	public static final byte[] CLIENT_NUMBER_HEADER = { (byte) 0xE0, (byte) 0x2E, (byte) 0x04, (byte) 0x00 };
	public static final byte[] CHARACTER_INFORMATION_HEADER = { (byte) 0xE1, (byte) 0x2E, (byte) 0x5E, (byte) 0x05 };
	
	public static final byte[] PLAYERS_HEADER = { (byte) 0x27, (byte) 0x27, (byte) 0x13, (byte) 0x00 };
	
	public static final byte[] OK_HEADER = { (byte) 0x46, (byte) 0x2F, (byte) 0x20, (byte) 0x00 };
	public static final byte[] OK_PACKET = { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00 };
	
	public static final byte[] SERVER_CLIENT_CHECK_1 = { (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00 };
	public static final byte[] SERVER_CLIENT_CHECK_2 = { (byte) 0xCC };
	public static final byte[] SERVER_CLIENT_CHECK_ANWSER = { (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0xCC };
	
	public static final byte[] NULLBYTE = { (byte) 0x00 };
	public static String longnullbyte = "";
	
	static {
		final String nullbyte = Util.isoString(NULLBYTE);
		final StringBuilder nullbyte_builder = new StringBuilder();
		for (int i = 0; i < 1372; i++)
			nullbyte_builder.append(nullbyte);
		longnullbyte = nullbyte_builder.toString();
	}
	
	public static int fake_i = 0;
	
	protected ServerSocket serverSocket;
	protected Vector<ChannelServerConnection> clientConnections;
	
	protected final int port, timeout;
	private boolean stop;
	
	public ChannelServer(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
		this.clientConnections = new Vector<ChannelServerConnection>();
	}
	
	@Override
	public void start() {
		stop = false;
		super.start();
	}
	
	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(this.port);
			this.serverSocket.setSoTimeout(this.timeout);
			Main.logger.log("ChannelServer", "listening");
			final Lobby lobby = new Lobby(this);
			
			while (!stop) {
				try {
					final Socket socket = this.serverSocket.accept();
					// if(!Main.getip(socket).equals("127.0.0.1")){
					Main.logger.log("ChannelServer", "client connection from " + socket.getRemoteSocketAddress());
					final ChannelServerConnection socketConnection = new ChannelServerConnection(socket, this, lobby);
					clientConnections.add(socketConnection);
					socketConnection.start();
					// }
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
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
	
	@Override
	protected void finalize() {
		try {
			this.serverSocket.close();
			this.stop = true;
			Main.logger.log("ChannelServer", "stopped");
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
	public boolean removeClient(SocketAddress remoteAddress) {
		try {
			for (int i = 0; i < this.clientConnections.size(); i++) {
				final ChannelServerConnection con = this.clientConnections.get(i);
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
	
	public boolean getStopped() {
		return this.stop;
	}
	
	public int getClientCount() {
		return this.clientConnections.size();
	}
	
}
