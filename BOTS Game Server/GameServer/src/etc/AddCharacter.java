package etc;

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
		final String local_ip = java.net.Inet4Address.getLocalHost().toString();
		final String LOGIN_IP = local_ip.substring(0, 0) + local_ip.substring(1);
		final String LOGIN_EMAIL = "name@site.com";
		
		
		final java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat dformat = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		
		final String query = "INSERT INTO `bout_users` (id, username, password, coins, banned, online, current_ip, logincount, last_ip, lastlogin, email)"
				+ "VALUES ("
				+ LOGIN_ID + ",\"" + LOGIN_USERNAME + "\",\"" + LOGIN_PASSWORD + "\"," + COINS + "," + LOGIN_BANNED + "," + LOGIN_ALLOG + ",\"" + LOGIN_IP + "\"," + LOGIN_COUNT + ",\"" + LOGIN_IP + "\",'" + dformat.format(date) + "',\"" + LOGIN_EMAIL + "\");";
		
		final SQLDatabase db = new SQLDatabase("");
		db.start();
		db.doupdate(query);
		
	}
	
    private static String md5hash(String text) {
        try {
        MessageDigest md = null;
        byte[] encryptMsg = null;
        try {
             md = MessageDigest.getInstance("MD5");
             encryptMsg = md.digest(text.getBytes("ISO8859-1"));
        }
        catch (NoSuchAlgorithmException e) {
        }
        String swap = "";
        String byteStr = "";
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i <= encryptMsg.length - 1; i++) {
            byteStr = Integer.toHexString(encryptMsg[i]);
            switch (byteStr.length()) {
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
        String hash = strBuf.toString();
        return hash;
        } catch(Exception e){

        }
        return null;
     }
	
}
