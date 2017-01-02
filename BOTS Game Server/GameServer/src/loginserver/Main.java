/*
 * Main.java
 * This file executes the main server events.
 */

package loginserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.Logger;
import shared.SQLDatabase;

/**
 * Start the server.
 */
public class Main {

	public static final boolean DEBUG = true;
	private static final int loginPort = 11000;

	public static final String SESSION_LOG_DIR = "log";
	public static Logger logger;
	
	public static LoginServer loginServer;
	public static LoginServerGUI gui;

	public static SQLDatabase sql = new SQLDatabase("LoginServer");

	public static String getip(Socket sock) {
		final String s = sock.getInetAddress().toString();
		return s.substring(0, 0) + s.substring(1);
	}
	
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
	
	/**
	 * Starts server and the GUI for messages.
	 */
	public static void main(String[] args) {
		try {
			gui = new LoginServerGUI();
			loginServer = new LoginServer(loginPort);
			
			final PrintStream guisession = createGuiSessionStream();
			final File session = createSessionLog();
			logger = new Logger(new PrintStream[] {System.out, new PrintStream(session), guisession});
			System.setOut(null);
			System.setIn(null);
			
			gui.setTitle("Bots Login Server");
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);

			sql.start();

			loginServer.start();

			gui.startUpdateTimer();

			final RoomUDPServer roomserver = new RoomUDPServer();
			roomserver.start();

			ChannelServer.main();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
