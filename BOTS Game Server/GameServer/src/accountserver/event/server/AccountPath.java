package accountserver.event.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Vector;

import accountserver.Account;
import accountserver.Main;
import accountserver.ServerConnection;
import accountserver.event.client.LoginEvent;
import accountserver.event.client.RegisterEvent;

public class AccountPath extends ServerPath {

	protected ServerSocket socketServer;
	protected Vector<ServerConnection> clientConnections;
	
	public AccountPath(int port, int timeout) {
		super(port, timeout);
		clientConnections = new Vector<ServerConnection>();
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
					if (Account.LOGIN_REQUEST_STR.equals(header)) {
						new LoginEvent(socketConnection).start();
					} else if (Account.REGISTER_REQUEST_STR.equals(header)) {
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
	
	public int getClientCount() {
		return this.clientConnections.size();
	}

}
