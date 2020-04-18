package breakingxp;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Potatoe implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void breakPotatoe(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.POTATO) {
			// potatoe growth max
			if (block.getData() == (byte) 7) {
				event.getPlayer().giveExp(1);
				if (event.getPlayer().getInventory().contains(Material.POTATO)) {

					block.setData((byte) 0);
					block.setType(Material.POTATO);
				} else {

					block.breakNaturally();
				}
			}
		}
	}
}
