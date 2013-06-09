package itemmovesql;

import java.sql.*;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class DBUtils {

	private ItemMoveSQLConfig config;

	DBUtils(Main main, ItemMoveSQLConfig config) {
		this.config = config;
	}

	private Logger log = Bukkit.getLogger();

	public Connection getConenction() {
		return InitConnection();
	}

	public void CreateNeeded() {
		Connection connection = null;
		try {
			if (config.checkdb) {
				connection = DriverManager.getConnection(config.address,
						config.login, config.pass);
				log.info("[Аукцион] Connected to mysql server, creating database if not exists");
				Statement st = connection.createStatement();
				st.executeUpdate("CREATE DATABASE IF NOT EXISTS "
						+ config.dbname);
				st.close();
				connection.close();
			}
			connection = DriverManager.getConnection(config.address
					+ config.dbname, config.login, config.pass);
			Statement st = connection.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS itemstorage"
					+ "("
					+ "keyint int unsigned not null auto_increment primary key,"
					+ "playername varchar(255)," + "itemid int,"
					+ "itemsubid int," + "amount int" + ");");
			st.close();
			log.info("[Аукцион] Connected to mysql server and database");
			connection.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Connection InitConnection() {
		try {
			Connection connection = null;

			connection = DriverManager.getConnection(config.address
					+ config.dbname, config.login, config.pass);

			return connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
