package itemmovesql;

import java.sql.*;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class DBUtils {

	private Main main;
	private ItemMoveSQLConfig config;
	
	DBUtils(Main main,ItemMoveSQLConfig config)
	{
		this.main = main;
		this.config = config;
	}
	
	private Connection connection = null;
	private Logger log = Bukkit.getLogger();
	
	public Connection getConenction()
	{
		if (connection == null)
		{ InitConnection(); }
		return connection;
	}
	
	public  void InitConnection()
			{
				if (connection == null)
				{
					try {
					boolean dbfound = false;
					 connection = 	DriverManager.getConnection(config.address,config.login,config.pass);
					 log.info("[ItemMoveMSQL] Connected to mysql server, checking database");
						ResultSet rs =  connection.getMetaData().getCatalogs();
						while( rs.next () ) {
							if ( rs.getString(1).equals(config.dbname))
							{
							log.info("[ItemMoveMSQL] Database found, connecting to it");
							dbfound = true;	
							}
							}
							rs.close();
					if (!dbfound)
					{
						 log.info("[ItemMoveMSQL] Database not found, creating one for you");
						Statement st = connection.createStatement();
						st.executeUpdate("CREATE DATABASE "+config.dbname);
						st.close();
					}
					CloseConnection();
					 connection = 	DriverManager.getConnection(config.address+config.dbname,config.login,config.pass);
					Statement st = connection.createStatement();
					st.executeUpdate(
							"CREATE TABLE IF NOT EXISTS itemstorage" +
							"(" +
							"keyint int unsigned not null auto_increment primary key," +
							"playername varchar(255)," +
							"itemid varchar(255)," +
							"amount int" +
							");"
						);
					st.close();
					 log.info("[ItemMoveMSQL] Connected to mysql server and database");

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
	public  void CloseConnection()
	{
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
