package toolz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemsManager implements Listener {
	public static Main main = null;

	@SuppressWarnings("deprecation")
	//when player hit 
	public static void open_market_edit(Player player, String item,String buy_price,String sell_price, Main instance) {
		main = instance;
		Inventory market_edit = Bukkit.createInventory(null, (6*9), "Set item to choose");
		ItemStack market_edit_cancel = new ItemStack(Material.TNT,1);
		ItemMeta item_key_meta = market_edit_cancel.getItemMeta();
		item_key_meta.setLore(Arrays.asList(item,buy_price,sell_price));
		market_edit_cancel.setItemMeta(item_key_meta);
		market_edit.setItem(0, Market.setItemNameFromItemStack(market_edit_cancel,"§cAnnuler§r"));
		
		Set<String> keys = main.getConfig().getConfigurationSection("items").getKeys(false);
		for (String key : keys) {
			
			@SuppressWarnings("rawtypes")
			
			ArrayList item_conf = (ArrayList) main.getConfig().get("items."+key);
			//set item name
			
			market_edit.setItem(Integer.parseInt(key)+2, new ItemStack((int) item_conf.get(0)));
			
		}
		player.openInventory(market_edit);
	}
	@EventHandler
	public static void marketeditClickAction(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked(); 
		//ItemStack clicked = event.getCurrentItem(); 
		if (event.getView().getTopInventory().getName().equals("Set item to choose")) {
			if (event.getRawSlot() > 1 && event.getRawSlot() < (6*9) -1) {
				//if case is not air
				if (Integer.parseInt(event.getView().getTopInventory().getItem(0).getItemMeta().getLore().get(0)) != 0) {

					
					ArrayList<Object> list = new ArrayList<>(Arrays.asList(Integer.parseInt(event.getView().getTopInventory().getItem(0).getItemMeta().getLore().get(0)),
							Double.parseDouble(event.getView().getTopInventory().getItem(0).getItemMeta().getLore().get(1)),
							Double.parseDouble(event.getView().getTopInventory().getItem(0).getItemMeta().getLore().get(2))));
					main.getConfig().set("items."+(event.getRawSlot()-2), list);
					main.saveConfig();
					//Make sur reloaed values
					main.reload_values();
					player.closeInventory();
					player.sendMessage("§aSuccessfuly set§r");
				} else {
					main.getConfig().set("items."+(event.getRawSlot()-2), null);
					main.saveConfig();
					//Make sur reloaed values
					main.reload_values();
					player.closeInventory();
					player.sendMessage("§aSuccessfuly deleted§r");
				}
			}
			event.setCancelled(true);
		}
		
	}
}
