package toolz;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import craftingxp.*;
import breakingxp.*;
import org.bukkit.Material;
public final class Main extends JavaPlugin {
	public static Location spawnpoint = new Location(null, 0,0,0);
	public static String toolz_version = "0.3 Beta";
	public static boolean market_disabled = false;
	//Vault
	public void reload_values() {
		this.reloadConfig();
		

		//reload configs
		Main.spawnpoint = new Location(getServer().getWorld((String) getConfig().get("spawnpoint.world")),(int) getConfig().get("spawnpoint.x"),(int) getConfig().get("spawnpoint.y"),(int) getConfig().get("spawnpoint.z"));
		Market.reload_market_buy();
		Market.reload_marketsell();

		//close inventory for player opened /market
		
		for (Player player : getServer().getWorlds().get(0).getPlayers() ) {
			if (!player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(player.getInventory().getName())) {
				player.closeInventory();
			}
		}
	}
	public static Economy econ = null;
	@Override
	   public void onEnable() {
		if (!setupEconomy() ) {
            getLogger().info("Vault not detected. Cant initialize");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		this.reloadConfig();
		if (!getConfig().isSet("spawnpoint")) {

			getConfig().set("spawnpoint.x",271);
			getConfig().set("spawnpoint.y",67);
			getConfig().set("spawnpoint.z",9);
			getConfig().set("spawnpoint.world","world");

			this.saveConfig();
		}
		if (!getConfig().isSet("items")) {
            getLogger().info("To continue, you must to set items in config.yml");
            getServer().getPluginManager().disablePlugin(this);
			this.saveConfig();
		}
		//Set default spawnpoint
		Main.spawnpoint = new Location(getServer().getWorld((String) getConfig().get("spawnpoint.world")),(int) getConfig().get("spawnpoint.x"),(int) getConfig().get("spawnpoint.y"),(int) getConfig().get("spawnpoint.z"));

		getServer().getPluginManager().registerEvents(new Events(), this);

		getServer().getPluginManager().registerEvents(new Market(this), this);
		getServer().getPluginManager().registerEvents(new ItemsManager(), this);
		//Crafting Xp Dependencies
		getServer().getPluginManager().registerEvents(new Bread(), this);

		getServer().getPluginManager().registerEvents(new Potatoe(), this);
		reload_values();
	}
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			
		if (cmd.getName().equalsIgnoreCase("spawn")) {
			Player target = (Player) sender;
			if (args.length == 0) {
		        target.teleport(Main.spawnpoint);
			} else {
				Player entity = Bukkit.getServer().getPlayer(args[0]);
			    if (target != null && sender.isOp()) {
			    	entity.teleport(Main.spawnpoint);
			    } else {
			    	sender.sendMessage("§c"+args[0] + " is not online!§r");
			    }
			}
			
			return true;
		} else if(cmd.getName().equalsIgnoreCase("market")) {
			if (!Main.market_disabled || sender.hasPermission("toolz.bypass")) {

				Player target = (Player) sender;
				target.openInventory(Market.market_home);
			} else {
				sender.sendMessage("§cUn administrateur à temporairement désactivé le market§r");
			}
		}else if (cmd.getName().equalsIgnoreCase("invec")) {
			Player target = (Player) sender;
			target.openInventory(target.getEnderChest());
		}
		else if(cmd.getName().equalsIgnoreCase("toolz")) {
		
			if (sender.hasPermission("toolz.basic")) {
				
				if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
					sender.sendMessage("§aToolz §c"+Main.toolz_version+"§a Copyright 2020");
					sender.sendMessage("Base plugin for cyteria surviv");
					
					
				} else if(args[0].equalsIgnoreCase("reload")) {
					reload_values();
				} else if(args[0].equalsIgnoreCase("disablemarket")) {
					Main.market_disabled = Boolean.parseBoolean(args[1]);
				}
				
				else if (args[0].equalsIgnoreCase("setspawn")) {
					this.reloadConfig();
					Player target = (Player) sender;
					getConfig().set("spawnpoint.x",target.getLocation().getX());
					getConfig().set("spawnpoint.y",target.getLocation().getX());
					getConfig().set("spawnpoint.z",target.getLocation().getZ());
					getConfig().set("spawnpoint.world",target.getLocation().getWorld().getName());
					this.saveConfig();
					reload_values();
				} else if (args[0].equalsIgnoreCase("removeitem") || args[0].equalsIgnoreCase("r")) {
					Player target = (Player) sender;
						ItemsManager.open_market_edit(target, "0","0.0","0.0",this);
				}else if (args[0].equalsIgnoreCase("setitem") || args[0].equalsIgnoreCase("s")|| args[0].equalsIgnoreCase("a")) {
					Player target = (Player) sender;
					if (args.length == 4) {
						if (Material.getMaterial(Integer.parseInt(args[1])) == null) {
							target.sendMessage("§c"+args[1]+" is not a valid id§r");
						} else {

							ItemsManager.open_market_edit(target, args[1],args[2],args[3],this);
						}
					} else {
						target.sendMessage("Check params : /toolz setitem [id] [buy_price] [sell_price]");
					}
				} else if (args[0].equalsIgnoreCase("setitem")) {
					//getConfig().get
				}
					
				else if(args[0].equalsIgnoreCase("getid")) {
					Player target = (Player) sender;
					target.sendMessage("Item id is : "+target.getInventory().getItemInHand().getTypeId());
				}
				else {
					sender.sendMessage("§cInvalid command§r");
				}
			} else {
				sender.sendMessage("§cYou are not permitted to do that !§r");
			}
		}
		return true;
	}
}
