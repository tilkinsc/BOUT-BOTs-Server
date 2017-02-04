package server.event.account.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import server.Main;
import server.account.Channel;
import server.event.ServerPath;
import shared.Util;

public class ChannelPath extends ServerPath {

	
	public ChannelPath(int port, int timeout) {
		super(port, timeout);
	}

	@Override
	public void run() {
		final byte[] receiveData = new byte[12];
		byte[] sendData = new byte[340];
		String reqString;
		int client_port;
		InetAddress IPAddress;
		
		try {
			final DatagramSocket serverSocket = new DatagramSocket(port);
			serverSocket.setSoTimeout(timeout);
			DatagramPacket sendPacket;
			Main.logger.log("ChannelList", "Channel List is walking on " + port);
			
			while (!stop) {
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try {
					serverSocket.receive(receivePacket);
				} catch (SocketTimeoutException e) {
					continue;
				}
				
				reqString = Util.isoString(receivePacket.getData());
				if (reqString.startsWith("\u00FA\u002A\u0002")) {
					Main.logger.log("ChannelList", "Channel List Requested.");
					Channel.getChannels(); // probably could add a 'channel list updated' to avoid getting over and over and over again
					
					String channel_packet = Util.isoString(Channel.CHANNEL_HEADERBYTE);
					for (int i = 0; i < Channel.channel_i; i++)
						channel_packet += Channel.channel_detail[i];
					
					for (int i = 0; i < 12 - Channel.channel_i; i++)
						channel_packet += Util.isoString(Channel.CHANNEL_EMPTY);
					
					for (int i = 0; i < 4; i++)
						channel_packet += Util.isoString(Channel.CHANNEL_FOOTER);
					
					sendData = channel_packet.getBytes("ISO8859-1");
					IPAddress = receivePacket.getAddress();
					client_port = receivePacket.getPort();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, client_port);
					serverSocket.send(sendPacket);
					
					Main.logger.log("ChannelList", "Sent Channels");
				}
			}
			serverSocket.close();
		} catch(Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}

}
