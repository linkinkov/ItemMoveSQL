package itemmovesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements CommandExecutor {
	
	private DBUtils dbutils;
	private ItemMoveSQLConfig config;
	private Main thisclass = this;

	public void onEnable()
	{
		config = new ItemMoveSQLConfig();
		dbutils = new DBUtils(this, config);
		dbutils.CreateNeeded();
	}
	
	public void onDisable()
	{
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
				if (player.getItemInHand() !=null && player.getItemInHand().getType() != Material.AIR) {
				player.sendMessage("[ItemMoveSQL] Выполняем запрос на добавление вещи в базу");
				

				final ItemStack iteminhand = player.getItemInHand();
				player.setItemInHand(null);
			
				
				Runnable additemtodb = new Runnable()
				{
					private String playername = player.getName();
					private int itemid = iteminhand.getTypeId();
					private int subdurabid = iteminhand.getDurability();
					private int amount = iteminhand.getAmount();
					@Override
					public void run() {
						try {
						    Statement st;
						    Connection conn = dbutils.getConenction();
							st = conn.createStatement();
						    ResultSet result = st.executeQuery("SELECT COUNT(keyint) FROM itemstorage WHERE playername = '"+playername+"'");
						     result.next();
						     int curiam =  result.getInt(1);
						     result.close();
						     if (curiam <  config.maxitems)
						     {
						    	st.executeUpdate("INSERT INTO itemstorage (playername, itemid, itemsubid, amount) VALUES ('"+playername+"', '"+itemid+"', '"+subdurabid+"', '"+amount+"')");
						    	player.sendMessage("[ItemMoveSQL] Предмет успешно добавлен в базу");
						    	st.close();
						     }
						     else {
						    	 st.close();
						    	 Bukkit.getPlayer(playername).sendMessage("[ItemMoveSQL] Вы уже положили максимум вещей в базу, возвращаем вам вещь в инвентарь");
						    	 Bukkit.getScheduler().scheduleSyncDelayedTask(thisclass, new Runnable()
						    	 {

									@Override
									public void run() {
									ItemStack item = new ItemStack(itemid,subdurabid);
									item.setAmount(amount);
									Bukkit.getPlayer(playername).getInventory().addItem(item);
									
									}
						    		 
						    	 });
						     }
						     
						     conn.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
	
					}
				};
				Bukkit.getScheduler().scheduleAsyncDelayedTask(this, additemtodb);

				} else {
					player.sendMessage("[ItemMoveSQL] Нельзя добавлять пустой итем в базу");
				}
				
				
				

			}
				
			}
		return false;
	}
	
}
