package shared;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SQLDatabase {

	protected static Properties sqldata = new Properties();
	protected static Connection con;
	protected static Statement st;

	protected static String ip, port, user, pass, database;

	private static void loadconfigs() {
		try {
			final FileInputStream fin = new FileInputStream("configs/mysql.conf");
			sqldata.load(fin);
			fin.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		ip = sqldata.getProperty("MySQL_ip");
		port = sqldata.getProperty("MySQL_port");
		user = sqldata.getProperty("MySQL_id");
		pass = sqldata.getProperty("MySQL_pw");
		database = sqldata.getProperty("MySQL_db");
	}

	public static void start() {
		loadconfigs();
		
		try {
			// Load the JDBC driver
			final String driverName = "org.gjt.mm.mysql.Driver";
			
			Class.forName(driverName);

			final String url = "jdbc:mysql://" + ip + "/" + database;
			
			con = DriverManager.getConnection(url, user, pass);
			if (con.isClosed())
				throw new Exception("SQLDatabase did not connect!");
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
