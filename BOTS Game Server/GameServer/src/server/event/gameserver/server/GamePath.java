package server.event.gameserver.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Vector;

import server.Main;
import server.event.ServerPath;
import server.gameserver.GameServerConnection;
import server.gameserver.Lobby;

public class GamePath extends ServerPath {

	protected ServerSocket serverSocket;
	public Vector<GameServerConnection> clientConnections;
	
	public GamePath(int port, int timeout) {
		super(port, timeout);
		this.clientConnections = new Vector<GameServerConnection>();
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
					final GameServerConnection socketConnection = new GameServerConnection(socket, this, lobby);
					clientConnections.add(socketConnection);
					socketConnection.start();
					// }
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
			this.serverSocket.close();
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
	public boolean removeClient(SocketAddress remoteAddress) {
		try {
			for (int i = 0; i < this.clientConnections.size(); i++) {
				final GameServerConnection con = this.clientConnections.get(i);
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
	
}
