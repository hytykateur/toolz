package toolz;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
public class Events implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
    public void blockDrawersInEnderChest(InventoryClickEvent event) {
		Player current_player = (Player) event.getWhoClicked();
    	if (event.getInventory().getName() == "container.enderchest") {
    		//If item is a drawer
    		if (event.getCurrentItem().getTypeId() >= 519 && event.getCurrentItem().getTypeId() <= 525) {
    			current_player.sendMessage("§cLes drawers sont interdits dans les enderchests§r");
        		event.setCancelled(true);
        	
    		}
    		//If item is money
    		else if (event.getCurrentItem().getTypeId() == 4478) {
    			current_player.sendMessage("§cLa money est interdite dans les enderchests§r");
    			event.setCancelled(true);
    		}
    	}
	}
	
	@EventHandler
	public void ifPlayerFallDown(PlayerMoveEvent event) {
		if (event.getPlayer().getLocation().getY() < -30) {
			event.getPlayer().teleport(Main.spawnpoint);
		}
	}
	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		event.setJoinMessage("");
		for (Player player : event.getPlayer().getWorld().getPlayers() ) {
			if (player.isOp()) {
				player.sendMessage(event.getPlayer().getDisplayName()+" s'est connecté");
			}
		}
		event.getPlayer().sendMessage("§cBIENVENUE SUR LE SERVEUR SURVIE !!§r");
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		event.setQuitMessage("");
		for (Player player : event.getPlayer().getWorld().getPlayers() ) {
			if (player.isOp() || player.hasPermission("toolz.joinevent")) {
				player.sendMessage(event.getPlayer().getDisplayName()+" s'est déconnecté");
			}
		}
	}

}