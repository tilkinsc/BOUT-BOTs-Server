package loginserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RoomUDPServer extends Thread {

	@Override
	public void run() {
		try {
			final DatagramSocket socket = new DatagramSocket(11011);
			boolean stop = false;
			while (!stop) {

				final DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				socket.receive(packet);

				final InetAddress address = packet.getAddress();
				final int port = packet.getPort();
				final byte[] data = packet.getData();

				final String datan = new String(data, "ISO8859-1");
				if (datan.startsWith("\u00C9\u0000")) {
					Main.logger.log("RoomUDPServer", "Save port " + port + " of IP " + address.toString().substring(1));
					Main.sql.doupdate("UPDATE `rooms` SET `port`=" + port + " WHERE `ip`='"
							+ address.toString().substring(1) + "' AND `port`=0");
				}

			}

			socket.close();
		} catch (Exception e) {

		}
	}
}