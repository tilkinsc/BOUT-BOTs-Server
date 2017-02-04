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
import shared.Logger;

public class GamePath extends ServerPath {

	protected ServerSocket serverSocket;
	public Vector<GameServerConnection> clientConnections;
	
	private final Logger logger;
	
	public GamePath(Logger logger, int port, int timeout) {
		super(port, timeout);
		this.logger = logger;
		this.clientConnections = new Vector<GameServerConnection>();
	}
	
	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(this.port);
			this.serverSocket.setSoTimeout(this.timeout);
			logger.log("ChannelServer", "listening on " + this.port);
			final Lobby lobby = new Lobby(logger, this);
			
			while (!stop) {
				try {
					final Socket socket = this.serverSocket.accept();
					// if(!Main.getip(socket).equals("127.0.0.1")){
					logger.log("ChannelServer", "client connection from " + socket.getRemoteSocketAddress());
					final GameServerConnection socketConnection = new GameServerConnection(logger, socket, this, lobby);
					clientConnections.add(socketConnection);
					socketConnection.start();
					// }
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
			this.serverSocket.close();
		} catch (Exception e) {
			logger.log("Exception", e.getMessage());
		}
	}
	
	public boolean removeClient(SocketAddress remoteAddress) {
		try {
			for (int i = 0; i < this.clientConnections.size(); i++) {
				final GameServerConnection con = this.clientConnections.get(i);
				if (con.getRemoteAddress().equals(remoteAddress)) {
					this.clientConnections.remove(i);
					con.finalize();
					logger.log("LoginServer", remoteAddress + " removed");
					return true;
				}
			}
		} catch (Exception e) {
			logger.log("Exception", e.getMessage());
		}
		return false;
	}
	
	public void removeAllClients() {
		try {
			for (int i=0; i<this.clientConnections.size(); i++)
				this.clientConnections.get(i).finalize();
			this.clientConnections.clear();
		} catch (Exception e) {
			logger.log("Exception", e.getMessage());
		}
	}
	
}
