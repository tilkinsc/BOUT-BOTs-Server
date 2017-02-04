package loginserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import shared.Util;

public class ServerConnection {

	private final Socket socket;
	private final BufferedReader socketIn;
	private final PrintWriter socketOut;
	
	public ServerConnection(Socket socket) throws IOException {
		this.socket = socket;
		this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.socketOut = new PrintWriter(this.socket.getOutputStream(), true);
	}
	
	@Override
	public void finalize() {
		try {
			Main.loginserver.removeClient(this.getRemoteAddress());
			this.socket.close();
			Main.logger.log("LoginServerConnection", "Thread " + Thread.currentThread() + " client removed");
		} catch (final Exception e) {
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
	
	public String read() {
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
	
	public Socket getSocket() {
		return socket;
	}
	
	public PrintWriter getWriter() {
		return socketOut;
	}
	
	public BufferedReader getReader() {
		return socketIn;
	}
	
}