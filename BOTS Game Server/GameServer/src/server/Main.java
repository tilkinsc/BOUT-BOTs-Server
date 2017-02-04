package server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import server.account.event.server.AccountPath;
import server.account.event.server.ChannelPath;
import server.account.event.server.RoomPath;
import server.gameserver.ChannelServer;
import server.gui.ServerGui;
import shared.ConfigStore;
import static shared.ConfigStore.PropertyStructure;
import shared.Logger;
import shared.SQLDatabase;

public class Main {

	public static final String SESSION_LOG_DIR = "log_login";
	public static Logger logger;
	
	public static ServerGui gui;
	
	public static AccountPath accountpath;
	public static ChannelPath channelpath;
	public static RoomPath roomserver;
	
	public static Vector<ChannelServer> channelservers;
	
	public static PrintStream createGuiSessionStream() {
		final OutputStream os = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};
		return new PrintStream(os) {
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
		channelservers = new Vector<ChannelServer>();
		try {
			final PropertyStructure mysql = ConfigStore.loadProperties("configs/mysql.cfg");
			final PropertyStructure channels = ConfigStore.loadProperties("configs/channels.cfg");
			
			gui = new ServerGui();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			
			final PrintStream guisession = createGuiSessionStream();
			final File session = createSessionLog();
			logger = new Logger(new PrintStream[] {System.out, new PrintStream(session), guisession});
			
			accountpath = new AccountPath(11000, 5000);
			channelpath = new ChannelPath(11010, 5000);
			roomserver = new RoomPath(11011, 5000);
			
			gui.startUpdateTimer();
			
			SQLDatabase.loadconfig(mysql);
			SQLDatabase.start();
			accountpath.start();
			roomserver.start();
			channelpath.start();
			
			for (int i=0; i<Integer.valueOf(channels.getProperty("channels")); i++) {
				gui.addTab(channels.getProperty("name_"+i));
				
				//TODO: Connect logger
				final ChannelServer channelserver = new ChannelServer(Integer.valueOf(channels.getProperty("port_"+i)), Integer.valueOf(channels.getProperty("timeout_"+i)));
				channelserver.start();
				channelservers.add(channelserver);
				logger.log("Main", "Started new channel server");
			}
			
			logger.log("Main", "Login server started!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void invokeShutdown() {
		gui.dispose();
		for (int i=0; i<channelservers.size(); i++)
			channelservers.get(i).stopThread();
		accountpath.stopThread();
		logger.log("Main", "login closed");
		roomserver.stopThread();
		logger.log("Main", "room closed");
		channelpath.stopThread();
		logger.log("Main", "channel closed");
		try {
			accountpath.join();
			roomserver.join();
			channelpath.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SQLDatabase.close();
		logger.log("Main", "SQL closed");
		try {
			ConfigStore.saveProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.log("Main", "Properties saved");
		logger.flushAll();
		logger.closeAll();
	}
	
}