package itemmovesql;

import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
	
	private DBUtils dbutils;
	private ItemMoveSQLConfig config;

	public void onEnable()
	{
		config = new ItemMoveSQLConfig();
		dbutils = new DBUtils(this, config);
		dbutils.InitConnection();
	}
	
	public void onDisable()
	{
		dbutils.CloseConnection();
		dbutils = null;
		config = null;
	}
	
	
}
