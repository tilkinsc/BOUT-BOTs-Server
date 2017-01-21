package channelserver;

import java.io.PrintStream;
import java.net.Socket;

import shared.Logger;
import shared.SQLDatabase;

public class Main {

	public static Logger logger;
	
	public static final boolean DEBUG = true;
	public static ChannelServer channelServer;
	public static ChannelGameServerGUI gui;
	public static String str = new String();

	public static void debug(String label, String msg) {
		if (DEBUG && Main.gui != null)
			Main.gui.write(label + ": " + msg);
	}

	public static String getip(Socket sock) {
		final String s = sock.getInetAddress().toString();
		return s.substring(0, 0) + s.substring(1);
	}

	public static void main(String[] args) {
		try {
			final int ChannelPort = 11002;

			final PrintStream[] ps = new PrintStream[] {
					System.out
			};
			logger = new Logger(ps);
			
			channelServer = new ChannelServer(ChannelPort);
			channelServer.start();

			gui = new ChannelGameServerGUI(channelServer);
			gui.setTitle("Bots Channel Server!");
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			// if you remove, you will instantly die by a flying pig.
			debug("[Credits]", "Server Files Edited by Secured!");

			final String nullbyte = new String(ChannelServer.NULLBYTE, "ISO8859-1");
			for (int i = 0; i < 1372; i++)
				ChannelServer.longnullbyte += nullbyte;

			SQLDatabase.start();
			debug("[ChannelServer]", "Server Starting...!");
			debug("[ChannelServer]", "ChannelServer has jumped on port " + ChannelPort + "");
			debug("[ChannelServer]", "Server Started!");
		} catch (Exception e) {
			debug("Main", "Exception (main)" + e.getMessage());
			e.printStackTrace();
		}
	}

}
