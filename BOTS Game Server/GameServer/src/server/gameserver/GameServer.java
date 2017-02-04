package server.gameserver;

import shared.Util;

public class GameServer extends Thread {

	public static final byte[] PACKETS_HEADER = { (byte) 0x01, (byte) 0x00 };
	
	public static final byte[] BOT_CREATION_HEADER = { (byte) 0xE2, (byte) 0x2E, (byte) 0x02, (byte) 0x00 };
	public static final byte[] CREATE_BOT_USERNAME_TAKEN = { (byte) 0x00, (byte) 0x36 };
	public static final byte[] CREATE_BOT_USERNAME_ERROR = { (byte) 0x00, (byte) 0x33 };
	public static final byte[] CREATE_BOT_CREATED = { (byte) 0x01, (byte) 0x00 };
	
	public static final byte[] CLIENT_NUMBER_HEADER = { (byte) 0xE0, (byte) 0x2E, (byte) 0x04, (byte) 0x00 };
	public static final byte[] CHARACTER_INFORMATION_HEADER = { (byte) 0xE1, (byte) 0x2E, (byte) 0x5E, (byte) 0x05 };
	
	public static final byte[] PLAYERS_HEADER = { (byte) 0x27, (byte) 0x27, (byte) 0x13, (byte) 0x00 };
	
	public static final byte[] OK_HEADER = { (byte) 0x46, (byte) 0x2F, (byte) 0x20, (byte) 0x00 };
	public static final byte[] OK_PACKET = { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00 };
	
	public static final byte[] SERVER_CLIENT_CHECK_1 = { (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00 };
	public static final byte[] SERVER_CLIENT_CHECK_2 = { (byte) 0xCC };
	public static final byte[] SERVER_CLIENT_CHECK_ANWSER = { (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0xCC };
	
	public static final byte[] NULLBYTE = { (byte) 0x00 };
	public static String longnullbyte = "";
	
	static {
		final String nullbyte = Util.isoString(NULLBYTE);
		final StringBuilder nullbyte_builder = new StringBuilder();
		for (int i = 0; i < 1372; i++)
			nullbyte_builder.append(nullbyte);
		longnullbyte = nullbyte_builder.toString();
	}
	
	public static int fake_i = 0;
	
}
