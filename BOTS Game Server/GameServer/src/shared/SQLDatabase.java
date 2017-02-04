package shared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SQLDatabase {

	protected static Connection con;
	
	protected static String ip, port, user, pass, database;
	
	public static void loadconfig(Properties config) {
		ip = config.getProperty("MySQL_ip");
		port = config.getProperty("MySQL_port");
		user = config.getProperty("MySQL_id");
		pass = config.getProperty("MySQL_pw");
		database = config.getProperty("MySQL_db");
	}
	
	public static void start() {
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
