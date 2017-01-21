package loginserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;

import shared.SQLDatabase;
import shared.Util;

class ChannelServer extends Thread {

	// Packets
	public static final byte[] CHANNEL_HEADERBYTE = {(byte) 0xEE, (byte) 0x2C, (byte) 0x50, (byte) 0x01};
	public static final byte[] CHANNEL_FOOTER = {(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] CHANNEL_EMPTY = {(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] NULLBYTE = {(byte) 0x00};
	
	// Channel vars
	public String[] channel_detail = new String[12];
	public int[] channel_id = new int[12];
	public String[] channel_name = new String[12];
	public int[] channel_namelength = new int[12];
	public int[] channel_min = new int[12];
	public int[] channel_max = new int[12];
	public int[] channel_players = new int[12];
	public int channel_i = 0;
	
	// SQL Querys
	protected static final String GET_CHANNEL_QUERY = "SELECT * FROM bout_channels WHERE status=1 LIMIT 12";
	
	
	public final int port;
	public final int timeout;
	
	public ChannelServer(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
	}
	
	private boolean stop;
	
	@Override
	public void start() {
		stop = false;
		super.start();
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
					getChannels(); // probably could add a 'channel list updated' to avoid getting over and over and over again
					
					String channel_packet = Util.isoString(CHANNEL_HEADERBYTE);
					for (int i = 0; i < channel_i; i++)
						channel_packet += channel_detail[i];
					
					for (int i = 0; i < 12 - channel_i; i++)
						channel_packet += Util.isoString(CHANNEL_EMPTY);
					
					for (int i = 0; i < 4; i++)
						channel_packet += Util.isoString(CHANNEL_FOOTER);
					
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
		System.out.println("Ended");
	}
	
	public void stopThread() {
		stop = true;
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// probably could add a 'channel list updated' to avoid getting over and over and over again
	protected void getChannels() {
		try {
			final ResultSet rs = SQLDatabase.doquery(GET_CHANNEL_QUERY);
			if (channel_i != 0)
				channel_i = 0;
			
			final String nullbyte = Util.isoString(NULLBYTE);
			while (rs.next()) {
				channel_id[channel_i] = rs.getInt("id");
				channel_name[channel_i] = rs.getString("name");
				channel_namelength[channel_i] = channel_name[channel_i].length();
				
				channel_min[channel_i] = rs.getInt("minlevel");
				final byte[] MINBYTE = {(byte) channel_min[channel_i] };
				
				channel_max[channel_i] = rs.getInt("maxlevel");
				final byte[] MAXBYTE = {(byte) channel_max[channel_i] };
				
				channel_players[channel_i] = rs.getInt("players");
				final byte[] PLAYERSBYTE = {(byte) (channel_players[channel_i] & 0xff), (byte) ((channel_players[channel_i] >> 8) & 0xff) };
				
				channel_detail[channel_i] = Util.isoString(PLAYERSBYTE) + Util.isoString(MINBYTE) + Util.isoString(MAXBYTE) + channel_name[channel_i];
				
				for (int i = 0; i < 22 - channel_namelength[channel_i]; i++)
					channel_detail[channel_i] += nullbyte;
				
				channel_i++;
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
}
