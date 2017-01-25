package loginserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import shared.SQLDatabase;
import shared.Util;

public class RoomUDPServer extends Thread {

	private boolean stop;
	
	public final int port;
	public final int timeout;
	
	public RoomUDPServer(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
	}
	
	@Override
	public void start() {
		stop = false;
		super.start();
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

				final String datan = Util.isoString(data);
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
	
	public void stopThread() {
		this.stop = true;
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}