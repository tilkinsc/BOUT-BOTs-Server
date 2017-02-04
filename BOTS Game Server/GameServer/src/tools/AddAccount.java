package tools;

import java.io.IOException;
import java.net.InetAddress;

import shared.ConfigStore;
import shared.SQLDatabase;
import shared.Util;

public class AddAccount {

	
	// Adds a user to the database...
	public static void main(String[] args) throws IOException {
		final int COINS = 1000;
		final String LOGIN_USERNAME = "ydroque";
		final String LOGIN_PASSWORD = Util.sha512Digest("abc1234", "example");
		final int LOGIN_BANNED = 0;
		final int LOGIN_ALLOG = 0;
		final int LOGIN_COUNT = 0;
		final String local_ip = InetAddress.getLocalHost().toString();
		final String LOGIN_IP = local_ip.substring(0, 0) + local_ip.substring(1);
		final String LOGIN_EMAIL = "name@site.com";
		
		final java.util.Date date = new java.util.Date();
		final java.text.SimpleDateFormat dformat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		final String query = "INSERT INTO `bout_users` (username, password, coins, banned, online, current_ip, logincount, last_ip, lastlogin, email)"
				+ " VALUES (\""+ LOGIN_USERNAME + "\",\"" + LOGIN_PASSWORD + "\"," + COINS + ","
				+ LOGIN_BANNED + "," + LOGIN_ALLOG + ",\"" + LOGIN_IP + "\"," + LOGIN_COUNT + ",\"" + LOGIN_IP + "\",'"
				+ dformat.format(date) + "',\"" + LOGIN_EMAIL + "\");";
		System.out.println(query);
		
		SQLDatabase.loadconfig(ConfigStore.loadProperties("configs/mysql.cfg"));
		SQLDatabase.start();
		SQLDatabase.doupdate(query);
		SQLDatabase.close();
	}
	
}
