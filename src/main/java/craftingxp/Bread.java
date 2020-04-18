package craftingxp;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Bread implements Listener {

	private List<Integer> recipeBreadTest(Inventory inv) {
		for (int i = 0; i < 9; i += 3) {

			if (inv.getItem(i+1) != null && inv.getItem(i+2) != null && inv.getItem(i+3) != null) {
				if (inv.getItem(i+1).getType() == Material.WHEAT && inv.getItem(i+2).getType() == Material.WHEAT && inv.getItem(i+3).getType() == Material.WHEAT) {
					return Arrays.asList(i+1,i+2,i+3);
				}
			}
		}
		return Arrays.asList(0);
	}
	private int recipeBreadCount(Inventory inv) {
		List<Integer> breadTest = recipeBreadTest(inv);
		//get array of wheat count
		int[] wheat_count = {inv.getItem(breadTest.get(0)).getAmount(),inv.getItem(breadTest.get(1)).getAmount(),inv.getItem(breadTest.get(2)).getAmount()};
		Arrays.sort(wheat_count);

		return wheat_count[0];
	}
	@EventHandler
	public void breadXpInvClick(InventoryClickEvent event) {
		Player target = (Player) event.getWhoClicked();
		Inventory ctable = event.getView().getTopInventory();
		ItemStack clicked = event.getCurrentItem();
		if (clicked == null || clicked.hasItemMeta() == false || clicked.getItemMeta().hasDisplayName() == false) {
			if (ctable.getName() == "container.crafting" && event.getRawSlot() < 10) {
				if (recipeBreadTest(event.getInventory()).get(0) != 0) {
					if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {

						target.giveExp(recipeBreadCount(ctable));
					} else {
						target.giveExp(1);
					}
				}
			}
		}
	}

}
