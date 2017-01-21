package loginserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import shared.Logger;
import shared.SQLDatabase;

public class Main {

	public static final String SESSION_LOG_DIR = "log_login";
	public static Logger logger;
	
	public static LoginServer loginServer;
	public static LoginServerGUI gui;
	public static ChannelServer channelserver;
	
	public static RoomUDPServer roomserver;
	
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
			gui = new LoginServerGUI();
			
			final PrintStream guisession = createGuiSessionStream();
			final File session = createSessionLog();
			logger = new Logger(new PrintStream[] {System.out, new PrintStream(session), guisession});
			
			gui.setTitle("Bots Login Server");
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);

			loginServer = new LoginServer(11000, 5000);
			gui.startUpdateTimer();
			SQLDatabase.start();
			loginServer.start();

			roomserver = new RoomUDPServer(11011, 5000);
			roomserver.start();
			
			channelserver = new ChannelServer(11010, 5000);
			channelserver.start();
			
			logger.log("Main", "Login server started!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void invokeShutdown() {
		loginServer.stopThread();
		System.out.println("login closed");
		roomserver.stopThread();
		System.out.println("room closed");
		channelserver.stopThread();
		System.out.println("channel closed");
		SQLDatabase.close();
		System.out.println("SQL closed");
		logger.flushAll();
		logger.closeAll();
	}
	
}
