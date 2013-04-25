package itemmovesql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements CommandExecutor {
	
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
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String cl,
			String[] args) {
		String cname = command.getName();
		final Player player;
		if (sender instanceof Player ) {player = (Player) sender;} else {return false;}
		if (cname.equalsIgnoreCase("imsql"))
		{
			if (args.length == 1 && args[0].equalsIgnoreCase("add"))
			{
				player.sendMessage("[ItemMoveSQL] Кладём вещь в БД");
				

				
			
				
				Runnable additemtodb = new Runnable()
				{
					private String playername = player.getName();
					private int itemid = player.getItemInHand().getTypeId();
					private int subdurabid = player.getItemInHand().getDurability();
					private int amount = player.getItemInHand().getAmount();
					@Override
					public void run() {
						try {
						    Statement st;
							st = dbutils.getConenction().createStatement();
						      // ResultSet result = st.executeQuery("SELECT amount, keyint FROM itemstorage WHERE playername = '"+playername+"' AND itemid = "+itemid+" AND itemsubid = "+subdurabid);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
					}
				};


				
				
				player.setItemInHand(null);
			}
				
			}
		return false;
	}
	
}
