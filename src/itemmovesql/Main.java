﻿package itemmovesql;

import java.sql.Connection;
import java.sql.ResultSet;
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

	public void onEnable() {
		config = new ItemMoveSQLConfig();
		config.load();
		dbutils = new DBUtils(this, config);
		dbutils.CreateNeeded();
	}

	public void onDisable() {
		dbutils = null;
		config = null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String cl,
			String[] args) {
		String cname = command.getName();
		final Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			return false;
		}
		if (cname.equalsIgnoreCase("imsql")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("add")) {
				onCommandAdd(player, args);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("view")) {
				onCommandView(player, args);
			} else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
				onCommandGet(player, args);
			}

		}
		return false;
	}

	private boolean onCommandAdd(final Player player, String[] args) {
		if (player.getItemInHand() != null
				&& player.getItemInHand().getType() != Material.AIR) {
			player.sendMessage("[Аукцион] Предмет переносится в корзину...");

			final ItemStack iteminhand = player.getItemInHand();
			player.setItemInHand(null);

			Runnable additemtodb = new Runnable() {
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
						ResultSet result = st
								.executeQuery("SELECT COUNT(keyint) FROM itemstorage WHERE playername = '"
										+ playername + "'");
						result.next();
						int curiam = result.getInt(1);
						result.close();
						if (curiam < config.maxitems) {
							st.executeUpdate("INSERT INTO itemstorage (playername, itemid, itemsubid, amount) VALUES ('"
									+ playername
									+ "', '"
									+ itemid
									+ "', '"
									+ subdurabid + "', '" + amount + "')");
							player.sendMessage("[Аукцион] Предмет успешно добавлен в корзину");
							st.close();
						} else {
							st.close();
							Bukkit.getPlayerExact(playername)
									.sendMessage(
											"[Аукцион] Корзина заполнена, возвращаем вам предмет в инвентарь");
							Bukkit.getScheduler().scheduleSyncDelayedTask(
									thisclass, new Runnable() {

										@Override
										public void run() {
											ItemStack item = new ItemStack(itemid);
											item.setDurability((short) subdurabid);
											item.setAmount(amount);
											Bukkit.getPlayerExact(playername)
													.getInventory()
													.addItem(item);

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
			player.sendMessage("[Аукцион] Нечего добавить в корзину");
		}

		return true;
	}

	private boolean onCommandView(final Player player, String[] args) {
		player.sendMessage("[Аукцион] Выполняем запрос на просмотр предметов");

		Runnable viewitems = new Runnable() {
			String playername = player.getName();

			@Override
			public void run() {
				try {
					Statement st;
					Connection conn = dbutils.getConenction();
					st = conn.createStatement();
					ResultSet result = st
							.executeQuery("SELECT keyint, itemid, itemsubid, amount FROM itemstorage WHERE playername = '"
									+ playername + "'");
					while (result.next()) {
						player.sendMessage("[Аукцион]Номер вещи в корзине "
								+ result.getInt(1)
								+ " id вещи: "
								+ result.getInt(2)
								+ " subid/прочность вещи: "
								+ result.getInt(3)
								+ " количество вещей: "
								+ result.getInt(4));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};
		Bukkit.getScheduler().scheduleAsyncDelayedTask(thisclass, viewitems);
		return true;
	}

	private boolean onCommandGet(final Player player, String[] args) {
		if (args[1].matches("^-?\\d+$")) {
			final long getitemid = Long.valueOf(args[1]);
			player.sendMessage("[Аукцион] Получаем предмет из корзины...");
			Runnable getitem = new Runnable() {

				String playername = player.getName();
				long keyint = getitemid;

				@Override
				public void run() {
					try {
						Statement st;
						Connection conn = dbutils.getConenction();
						st = conn.createStatement(
								ResultSet.TYPE_SCROLL_SENSITIVE,
								ResultSet.CONCUR_UPDATABLE);
						ResultSet result = st
								.executeQuery("SELECT keyint ,itemid, itemsubid, amount FROM itemstorage WHERE playername = '"
										+ playername
										+ "' AND keyint = "
										+ keyint);
						if (result.next()) {
							final int itemid = result.getInt(2);
							final int itemsubid = result.getInt(3);
							final int amount = result.getInt(4);
							result.deleteRow();
							result.close();
							conn.close();
							Bukkit.getScheduler().scheduleSyncDelayedTask(
									thisclass, new Runnable() {
										String getplayername = playername;
										int getitemid = itemid;
										int getitemsubid = itemsubid;
										int getamount = amount;

										public void run() {
											ItemStack item = new ItemStack(getitemid);
											item.setDurability((short) getitemsubid);
											item.setAmount(getamount);
											Bukkit.getPlayerExact(getplayername)
													.getInventory()
													.addItem(item);
											Bukkit.getPlayerExact(getplayername)
													.sendMessage(
															"[Аукцион] Предмет получен");

										}
									});
						} else {
							Bukkit.getPlayerExact(playername)
									.sendMessage(
											"[Аукцион] Предмет вам не доступен!");
							result.close();
							conn.close();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			};
			Bukkit.getScheduler().scheduleAsyncDelayedTask(this, getitem);
			return true;
		}
		return false;
	}
}
