
package shared;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import loginserver.Main;

/**
 *
 * @author Marius
 */
public class SQLDatabase {

	protected String owner;

	protected Properties sqldata = new Properties();
	protected Connection con;
	protected Statement st;

	protected String ip, port, user, pass, database;

	public SQLDatabase(String createdby) {
		this.owner = createdby;
	}

	/**
	 * Loads the configs out of "configs/mysql.conf"
	 */
	private void loadconfigs() {
		try {
			final FileInputStream fin = new FileInputStream("configs/mysql.conf");
			sqldata.load(fin);
			fin.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ip = sqldata.getProperty("MySQL_ip");
		port = sqldata.getProperty("MySQL_port");
		user = sqldata.getProperty("MySQL_id");
		pass = sqldata.getProperty("MySQL_pw");
		database = sqldata.getProperty("MySQL_db");
	}

	public void start() {
		loadconfigs();

		/**
		 * setup the basic connection
		 */
		try {
			// Load the JDBC driver
			final String driverName = "org.gjt.mm.mysql.Driver";
			
			Class.forName(driverName);

			final String url = "jdbc:mysql://" + ip + "/" + database;
			
			con = DriverManager.getConnection(url, user, pass);
			if (!con.isClosed())
				Main.logger.log("SQLDatabase", "Successfully connected");
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}

	public ResultSet doquery(String query) {
		ResultSet rs = null;
		try {
			final Statement st = con.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			Main.logger.log("SQLDatabase", e.getMessage());
		}
		return rs;
	}

	public void doupdate(String query) {
		try {
			final Statement st = con.createStatement();
			st.executeUpdate(query);
		} catch (SQLException ex) {
			Main.logger.log("SQLDatabase", ex.getMessage());
		}
	}

}
