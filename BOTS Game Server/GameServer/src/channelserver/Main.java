package channelserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.Logger;
import shared.SQLDatabase;

public class Main {

	public static final String SESSION_LOG_DIR = "log_channel";
	public static Logger logger;
	
	public static ChannelServer channelserver;
	public static ChannelGameServerGUI gui;

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
		final boolean created = f.createNewFile();
		if(!created) {
			System.out.println("Session log already exists! Check your time!");
			System.exit(1);
		}
		return f;
	}

	public static void main(String[] args) {
		try {
			gui = new ChannelGameServerGUI();
			
			final PrintStream guisession = createGuiSessionStream();
			final File session = createSessionLog();
			logger = new Logger(new PrintStream[] {System.out, new PrintStream(session), guisession});
			
			gui.setTitle("Bots Channel Server");
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			
			channelserver = new ChannelServer(11002, 5000);
			gui.startUpdateTimer();
			SQLDatabase.start();
			channelserver.start();

			// wtf is this ?!?! :o oh init of longbyte in ChannelServer.. doesn't belong here
			final String nullbyte = new String(ChannelServer.NULLBYTE, "ISO8859-1");
			for (int i = 0; i < 1372; i++)
				ChannelServer.longnullbyte += nullbyte;

			logger.log("Main", "Login server started!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void invokeShutdown() {
		channelserver.stopThread();
		System.out.println("channel server closed");
		SQLDatabase.close();
		System.out.println("SQL closed");
		logger.flushAll();
		logger.closeAll();
	}

}
