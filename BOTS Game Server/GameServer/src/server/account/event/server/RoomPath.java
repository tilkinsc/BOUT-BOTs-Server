package server.account.event.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import server.Main;
import server.account.event.ServerPath;
import shared.SQLDatabase;

public class RoomPath extends ServerPath {

	
	public RoomPath(int port, int timeout) {
		super(port, timeout);
	}

	@Override
	public void run() {
		try {
			final DatagramSocket socket = new DatagramSocket(port);
			socket.setSoTimeout(timeout);
			
			while (!stop) {
				final DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				
				try {
					socket.receive(packet);
				} catch (SocketTimeoutException e) {
					continue;
				}
					
				final InetAddress address = packet.getAddress();
				final int port = packet.getPort();
				final byte[] data = packet.getData();

				final String datan = new String(data, "ISO8859-1");
				if (datan.startsWith("\u00C9\u0000")) {
					Main.logger.log("RoomUDPServer", "Save port " + port + " of IP " + address.toString().substring(1));
					SQLDatabase.doupdate("UPDATE `rooms` SET `port`=" + port + " WHERE `ip`='"
							+ address.toString().substring(1) + "' AND `port`=0");
				}

			}

			socket.close();
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
}