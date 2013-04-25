package itemmovesql;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemMoveSQLConfig {

	
	protected String address = "jdbc:mysql://localhost/";
	protected String dbname = "itemmovesqldb";
	protected String login = "root";
	protected String pass = "123789L4D2";
	public void load()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/ItemMoveSQL/config.yml"));
		address = config.getString("mysql.address",address);
		dbname = config.getString("mysql.dbname",dbname);
		login = config.getString("mysql.login",login);
		pass = config.getString("mysql.password",pass);
		
	}
}
