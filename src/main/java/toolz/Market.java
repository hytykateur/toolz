package toolz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Market implements Listener {
	public static Main main = null;
	
	public Market(Main instance) {
		main = instance;
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	public static ItemMeta setItemNameFromMeta(ItemStack item,String name) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		return itemmeta;
	}
	public static ItemStack setItemNameFromItemStack(ItemStack item,String name) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		item.setItemMeta(itemmeta);
		return item;
	}
	public static ItemStack buyedItems(ItemStack item, String price) {
		ItemMeta itemmeta = item.getItemMeta();
		
		itemmeta.setLore(Arrays.asList("ßcPrix ‡† l'unitÈ : ßr", price ));
		item.setItemMeta(itemmeta);
		//Set item price
		item.setAmount(1);
		return item;
	}

	@SuppressWarnings("deprecation")
	/*
	 * buy type :
	 * 0 = buy
	 * 1 = sell
	 */
	public static void setItemsFromConfig(Inventory inv,String buy_type) {
		try {
			main.reloadConfig();
			Set<String> keys = main.getConfig().getConfigurationSection("items").getKeys(false);

			for (String key : keys) {
				
				@SuppressWarnings("rawtypes")
				
				ArrayList item_conf = (ArrayList) main.getConfig().get("items."+key);
				//set item name
				if (buy_type == "buy") {

					inv.setItem(Integer.parseInt(key)+2, buyedItems(new ItemStack((int) item_conf.get(0)), Double.toString(
							(double) item_conf.get(1)
					)));
				} else if (buy_type == "sell") {
					inv.setItem(Integer.parseInt(key)+2, buyedItems(new ItemStack((int) item_conf.get(0)), Double.toString(
							(double) item_conf.get(2)
					)));
				}
			}
		} catch (Exception e) {
		}
	}
	public static ItemStack buyedItems_JustSetPrice(ItemStack item, String price) {
		ItemMeta itemmeta = item.getItemMeta();
		//Parse to string price + item amount
		itemmeta.setLore(Arrays.asList(Double.toString(Double.parseDouble(price)*item.getAmount())));
		item.setItemMeta(itemmeta);
		return item;
	}
	
	
	
	
	//Market Home inv
		public static Inventory market_home = Bukkit.createInventory(null, (6*9), "Marketplace by hytykateur");
		public static ItemStack home_sellingitem = new ItemStack(Material.STONE,1);
		public static ItemStack home_buyitem = new ItemStack(Material.GRASS,1);

		@SuppressWarnings("deprecation")
		public static ItemStack home_viewmoney = new ItemStack(4478,1);
		static {
			home_buyitem.setItemMeta(setItemNameFromMeta(home_buyitem,"ßbAcheter des itemsßr"));
			market_home.setItem(23, home_buyitem);
			home_sellingitem.setItemMeta(setItemNameFromMeta(home_sellingitem,"ßaVendre des itemsßr"));
			market_home.setItem(21, home_sellingitem);
			home_viewmoney.setItemMeta(setItemNameFromMeta(home_viewmoney,"ßaCliquez pour voir votre moneyßr"));
			market_home.setItem(4, home_viewmoney);

		}
		@EventHandler
		public void marketHome(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked(); 
			ItemStack clicked = event.getCurrentItem(); 
			Inventory inventory = event.getInventory(); 
			if (inventory.getName().equals(market_home.getName())) { 
				if (clicked.equals(home_buyitem)) {
					player.closeInventory();
					
					player.openInventory(market_buy);
				} else if (clicked.equals(home_sellingitem)) {
					player.openInventory(market_sell);
				}
				 else if (clicked.equals(home_viewmoney)) {
					player.sendMessage("ßaVous avez "+Main.econ.getBalance(player)+"$&r");
				}
				event.setCancelled(true);
			}
			
		}
		
		
		
		
	
		
		
		
		//Sell Items
		public static Inventory market_buy = Bukkit.createInventory(null, (6*9), "Buy items");

		public static ItemStack buy_return1 = new ItemStack(Material.APPLE,1);
		public static void reload_market_buy() {

			buy_return1.setItemMeta(setItemNameFromMeta(buy_return1,"ßcBackßr"));
			market_buy.setItem(0, buy_return1);
			//set automaticaly item from config
			setItemsFromConfig(market_buy,"buy");
		}
		static {
			reload_market_buy();
		}
		@SuppressWarnings("deprecation")
		@EventHandler
		public void marketSell(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked(); 
			ItemStack clicked = event.getCurrentItem(); 
			//Inventory inventory = event.getInventory(); 
			//Check if inventory is market buy and slot is in inventory and clicked is not AIR oof
			if (event.getView().getTopInventory().getName().equals(market_buy.getName()) && clicked.getType() != Material.AIR) { 
				if (event.getRawSlot() < (6*9) -1) {
					//Lets return to home
					if (clicked.equals(buy_return1)) {
						player.closeInventory();
						player.openInventory(market_home);
					} else {

						if(Double.parseDouble(clicked.getItemMeta().getLore().get(1)) != 0.0) {
							Inventory temp_buy = Bukkit.createInventory(null, 9, "Buy choose");
							temp_buy.setItem(0, buy_return1);
							//Set item price from precedent item
							temp_buy.setItem(3, buyedItems_JustSetPrice(new ItemStack(clicked.getType(),1),clicked.getItemMeta().getLore().get(1)));
							
							temp_buy.setItem(4, buyedItems_JustSetPrice(new ItemStack(clicked.getType(),5),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(5, buyedItems_JustSetPrice(new ItemStack(clicked.getType(),10),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(6, buyedItems_JustSetPrice(new ItemStack(clicked.getType(),20),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(7, buyedItems_JustSetPrice(new ItemStack(clicked.getType(),40),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(8, buyedItems_JustSetPrice(new ItemStack(clicked.getType(),64),clicked.getItemMeta().getLore().get(1)));
							player.openInventory(temp_buy);
						} else {
							player.sendMessage("ßcCet item ne peut pas √™tre achet√©ßr");
						}
					}
				}
				
				event.setCancelled(true);
			}
			//Buy item choose event
			if(event.getView().getTopInventory().getName().equals("Buy choose")) {
				if (event.getRawSlot() > 0 && event.getRawSlot() < 9 && clicked.getType() != Material.AIR) {
					if (Main.econ.getBalance(player.getName()) >= round(Double.parseDouble(clicked.getItemMeta().getLore().get(0)),2)) {
						player.sendMessage("Tu as achet√© "+clicked.getAmount()+" "+clicked.getType()+" pour "+clicked.getItemMeta().getLore().get(0)+"Ç¨");
						main.getServer().getConsoleSender().sendMessage(ChatColor.RED +"[MARKET] "+ChatColor.BLUE+player.getDisplayName()+ChatColor.RESET+" buy "+clicked.getAmount()+"x"+clicked.getType()+" for "+clicked.getItemMeta().getLore().get(0)+"$");
						//give some money for user
						Main.econ.withdrawPlayer(player.getName(), round(Double.parseDouble(clicked.getItemMeta().getLore().get(0)),2));
						player.getInventory().addItem(new ItemStack(clicked.getTypeId(),clicked.getAmount()));
						
					} else {
						player.sendMessage("ßcYou have not enough money !ßr");
					}
					//player return if not
				} else if(clicked.equals(buy_return1)) {
					player.closeInventory();
					player.openInventory(market_buy);
				}
				event.setCancelled(true);
			}
			
			
		}
		private static Inventory market_sell = Bukkit.createInventory(null, (6*9), "Sell items");
		public static void reload_marketsell() {

			buy_return1.setItemMeta(setItemNameFromMeta(buy_return1,"ßcBackßr"));
			market_sell.setItem(0, buy_return1);

			//set automaticaly item from config

			setItemsFromConfig(market_sell,"sell");
		}
		static {
			reload_marketsell();
		}
		@SuppressWarnings("deprecation")
		@EventHandler
		public void marketBuy(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked(); 
			ItemStack clicked = event.getCurrentItem(); 
			//Inventory inventory = event.getInventory(); 
			//Check if inventory is market buy and slot is in inventory and clicked is not AIR oof
			if (event.getView().getTopInventory().getName().equals(market_sell.getName()) && clicked.getType() != Material.AIR) { 
				//Lets return to home
				if (event.getRawSlot() < (6*9) -1) {
					if (clicked.equals(buy_return1)) {
						player.closeInventory();
						player.openInventory(market_home);
					} else {
						if(Double.parseDouble(clicked.getItemMeta().getLore().get(1)) != 0.0) {
							Inventory temp_buy = Bukkit.createInventory(null, 9, "Sell choose");
							temp_buy.setItem(0, buy_return1);
							//Set item price from precedent item
							temp_buy.setItem(3, buyedItems_JustSetPrice(new ItemStack(clicked.getTypeId(),1),clicked.getItemMeta().getLore().get(1)));
							
							temp_buy.setItem(4, buyedItems_JustSetPrice(new ItemStack(clicked.getTypeId(),5),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(5, buyedItems_JustSetPrice(new ItemStack(clicked.getTypeId(),10),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(6, buyedItems_JustSetPrice(new ItemStack(clicked.getTypeId(),20),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(7, buyedItems_JustSetPrice(new ItemStack(clicked.getTypeId(),40),clicked.getItemMeta().getLore().get(1)));

							temp_buy.setItem(8, buyedItems_JustSetPrice(new ItemStack(clicked.getTypeId(),64),clicked.getItemMeta().getLore().get(1)));
							player.openInventory(temp_buy);
						} else {
							player.sendMessage("ßcCet item ne peut pas √™tre vendußr");
						}
							
					}
				}
				
				event.setCancelled(true);
			}
			//Buy item choose event
			if(event.getView().getTopInventory().getName().equals("Sell choose")) {
				if (event.getRawSlot() > 0 && event.getRawSlot() < 9) {
					if (player.getInventory().contains(clicked.getTypeId(),clicked.getAmount()) && clicked.getType() != Material.AIR) {
						
						player.sendMessage("Tu as vendu "+clicked.getAmount()+" "+clicked.getType()+" pour "+clicked.getItemMeta().getLore().get(0)+"Ç¨");
						main.getServer().getConsoleSender().sendMessage(ChatColor.RED +"[MARKET] "+ChatColor.BLUE+player.getDisplayName()+ChatColor.RESET+" sell "+clicked.getAmount()+"x"+clicked.getType()+" for "+clicked.getItemMeta().getLore().get(0)+"$");
						player.getInventory().removeItem(new ItemStack[] {
		                        new ItemStack(Material.getMaterial(clicked.getTypeId()), clicked.getAmount()) });
						//give some money for user
						Main.econ.depositPlayer(player.getName(), round(Double.parseDouble(clicked.getItemMeta().getLore().get(0)),2));
					} else {
						player.sendMessage("ßcYou have not enough "+clicked.getType());
					}
						
					//player return if not
				} else if(clicked.equals(buy_return1)) {
					player.closeInventory();
					player.openInventory(market_sell);
				}

				event.setCancelled(true);
			}
			
			
		}
		  
}
