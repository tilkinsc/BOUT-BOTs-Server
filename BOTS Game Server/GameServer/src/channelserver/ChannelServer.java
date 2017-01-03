package channelserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Vector;

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

	public static int fake_i = 0;

	protected int port;
	protected ServerSocket serverSocket;
	protected boolean listening;
	protected Vector<ChannelServerConnection> clientConnections;

	public ChannelServer(int serverPort) {
		this.port = serverPort;
		this.listening = false;
		this.clientConnections = new Vector<ChannelServerConnection>();
	}

	public int getPort() {
		return this.port;
	}

	public boolean getListening() {
		return this.listening;
	}

	public int getClientCount() {
		return this.clientConnections.size();
	}

	protected void debug(String msg) {
		Main.debug("ChannelServer (" + this.port + ")", msg);
	}

	public boolean remove(SocketAddress remoteAddress) {
		try {
			debug("IP : " + remoteAddress);
			for (int i = 0; i < this.clientConnections.size(); i++) {
				final ChannelServerConnection client = this.clientConnections.get(i);
				debug("" + client.getRemoteAddress());
				if (client.getRemoteAddress().equals(remoteAddress)) {
					this.clientConnections.remove(i);
					debug("client " + remoteAddress + " removed");
					return true;
				}
			}
		} catch (Exception e) {
			debug("Exception (remove): " + e.getMessage());
		}

		return false;
	}

	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(this.port);
			this.listening = true;
			debug("listening");
			final Lobby lobby = new Lobby(this);

			while (this.listening) {
				final Socket socket = this.serverSocket.accept();
				// if(!Main.getip(socket).equals("127.0.0.1")){ // seems to
				// remove the ability to login from locahost
				debug("client connection from " + socket.getRemoteSocketAddress());
				final ChannelServerConnection socketConnection = new ChannelServerConnection(socket, this, lobby);
				clientConnections.add(socketConnection);
				socketConnection.start();
				// }
			}
		} catch (Exception e) {
			debug("Exception (run): " + e.getMessage());
		}
	}

	@Override
	protected void finalize() {
		try {
			this.serverSocket.close();
			this.listening = false;
			debug("stopped");
		} catch (Exception e) {
			debug("Exception (finalize): " + e.getMessage());
		}
	}
}
