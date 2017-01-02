package etc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import shared.SQLDatabase;

public class AddCharacter {

	// Adds a user to the database...
	public static void main(String[] args) throws UnknownHostException {

		final int LOGIN_ID = 0;
		final int COINS = 1000;
		final String LOGIN_USERNAME = "ydroque";
		final String LOGIN_PASSWORD = md5hash("abc1234");
		final int LOGIN_BANNED = 0;
		final int LOGIN_ALLOG = 0;
		final int LOGIN_COUNT = 0;
		final String local_ip = InetAddress.getLocalHost().toString();
		final String LOGIN_IP = local_ip.substring(0, 0) + local_ip.substring(1);
		final String LOGIN_EMAIL = "name@site.com";

		final java.util.Date date = new java.util.Date();
		final java.text.SimpleDateFormat dformat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		final String query = "INSERT INTO `bout_users` (id, username, password, coins, banned, online, current_ip, logincount, last_ip, lastlogin, email)"
				+ "VALUES (" + LOGIN_ID + ",\"" + LOGIN_USERNAME + "\",\"" + LOGIN_PASSWORD + "\"," + COINS + ","
				+ LOGIN_BANNED + "," + LOGIN_ALLOG + ",\"" + LOGIN_IP + "\"," + LOGIN_COUNT + ",\"" + LOGIN_IP + "\",'"
				+ dformat.format(date) + "',\"" + LOGIN_EMAIL + "\");";

		SQLDatabase.start();
		SQLDatabase.doupdate(query);

	}

	private static String md5hash(String text) {
		try {
			byte[] encryptMsg = null;
			try {
				final MessageDigest md = MessageDigest.getInstance("MD5");
				encryptMsg = md.digest(text.getBytes("ISO8859-1"));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				System.exit(1);
			}
			String swap = "";
			final StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i <= encryptMsg.length - 1; i++) {
				switch (Integer.toHexString(encryptMsg[i]).length()) {
				case 1:
					swap = "0" + Integer.toHexString(encryptMsg[i]);
					break;
				case 2:
					swap = Integer.toHexString(encryptMsg[i]);
					break;
				case 8:
					swap = (Integer.toHexString(encryptMsg[i])).substring(6, 8);
					break;
				}
				strBuf.append(swap);
			}
			return strBuf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

}
