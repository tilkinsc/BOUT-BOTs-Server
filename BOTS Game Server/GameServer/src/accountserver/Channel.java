package accountserver;

import java.sql.ResultSet;

import shared.SQLDatabase;
import shared.Util;

public class Channel {

	// Packets
	public static final byte[] CHANNEL_HEADERBYTE = {(byte) 0xEE, (byte) 0x2C, (byte) 0x50, (byte) 0x01};
	public static final byte[] CHANNEL_FOOTER = {(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] CHANNEL_EMPTY = {(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] NULLBYTE = {(byte) 0x00};
	
	// Channel vars
	public static String[] channel_detail = new String[12];
	public static int[] channel_id = new int[12];
	public static String[] channel_name = new String[12];
	public static int[] channel_namelength = new int[12];
	public static int[] channel_min = new int[12];
	public static int[] channel_max = new int[12];
	public static int[] channel_players = new int[12];
	public static int channel_i = 0;
	
	// SQL Querys
	protected static final String GET_CHANNEL_QUERY = "SELECT * FROM bout_channels WHERE status=1 LIMIT 12";
	
	public static boolean cached = false;
	
	// probably could add a 'channel list updated' to avoid getting over and over and over again
	public static void getChannels() {
		if(cached) return; // TODO: once I implement some form of control, I will change the caching
		cached = true;
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
