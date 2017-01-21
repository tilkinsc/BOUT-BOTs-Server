package channelserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.Logger;
import shared.SQLDatabase;

public class Main {

	public static final String SESSION_LOG_DIR = "log";
	public static Logger logger;
	
	public static ChannelServer channelserver;
	public static ChannelGameServerGUI gui;
	public static String str = new String();

	public static PrintStream createGuiSessionStream() {
		final OutputStream os = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};
		return new PrintStream(os){
			@Override
			public void println(String x) {
				try {
					Main.gui.write(x);
				} catch (Exception e) {
				}
			}
		};
	}
	
	public static File createSessionLog() throws IOException {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		final File f = new File(SESSION_LOG_DIR + "\\" + format.format(new Date()) + ".log");
		boolean created = f.createNewFile();
		if(!created) {
			System.out.println("Session log already exists! Check your time!");
			System.exit(1);
		}
		return f;
	}

	public static void main(String[] args) {
		try {
			final PrintStream[] ps = new PrintStream[] {
					System.out
			};
			logger = new Logger(ps);
			
			channelserver = new ChannelServer(11002, 5000);
			channelserver.start();

			gui = new ChannelGameServerGUI();
			gui.setTitle("Bots Channel Server!");
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			// if you remove, you will instantly die by a flying pig.
			logger.log("Credits", "Server Files Edited by Secured!");

			final String nullbyte = new String(ChannelServer.NULLBYTE, "ISO8859-1");
			for (int i = 0; i < 1372; i++)
				ChannelServer.longnullbyte += nullbyte;

			SQLDatabase.start();
			logger.log("[ChannelServer]", "Server Starting...!");
			logger.log("[ChannelServer]", "ChannelServer has jumped on port " + 11002 + "");
			logger.log("[ChannelServer]", "Server Started!");
		} catch (Exception e) {
			logger.log("Main", "Exception (main)" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void invokeShutdown() {
		
	}

}
