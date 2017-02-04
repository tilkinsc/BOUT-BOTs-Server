package shared;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SQLDatabase {

	protected static Connection con;
	
	protected static String ip, port, user, pass, database;
	
	private static void loadconfig() {
		final Properties config = new Properties();
		try {
			final FileInputStream fin = new FileInputStream("configs/mysql.conf");
			config.load(fin);
			fin.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		ip = config.getProperty("MySQL_ip");
		port = config.getProperty("MySQL_port");
		user = config.getProperty("MySQL_id");
		pass = config.getProperty("MySQL_pw");
		database = config.getProperty("MySQL_db");
	}
	
	public static void start() {
		loadconfig();
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + database, user, pass);
			if (con.isClosed())
				throw new Exception("SQLDatabase didn't connect!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void close() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ResultSet doquery(String query) {
		ResultSet rs = null;
		try {
			final Statement st = con.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void doupdate(String query) {
		try {
			final Statement st = con.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
