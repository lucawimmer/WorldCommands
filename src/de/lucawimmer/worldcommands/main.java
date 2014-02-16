package de.lucawimmer.worldcommands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener  {
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		if (!new File(getDataFolder(), "config.yml").exists()) {
		     saveDefaultConfig();
		}
		this.reloadConfig();
                
                try {
                    Metrics metrics = new Metrics(this);
                    metrics.start();
                } catch (IOException e) {}
                
		getLogger().info("Successfully loaded.");
	}	
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("worldcommands")){ 
    		if (sender instanceof ConsoleCommandSender) {
    			this.reloadConfig();
    			getLogger().info("The configuration file has been successfully reloaded.");
    		} else {
    			Player player = (Player) sender;
    			if (player.hasPermission("worldcommands.admin.reload")) {
    				this.reloadConfig();
    				String chatprefix = ChatColor.GOLD + "[" + ChatColor.RED + ChatColor.BOLD + "WorldCommands" + ChatColor.RESET + ChatColor.GOLD + "]" + ChatColor.RESET;
    				player.sendMessage(chatprefix + " The configuration file has been successfully reloaded.");
    				getLogger().info(player.getName() + " has reloaded the configuration file.");
    			} else {
    				String chatprefix = ChatColor.GOLD + "[" + ChatColor.RED + ChatColor.BOLD + "WorldCommands" + ChatColor.RESET + ChatColor.GOLD + "]" + ChatColor.RESET;
    				player.sendMessage(chatprefix + " You don't have permission to acces this command.");
    			}
    		}
    		return true;
    	}
    	return false; 
    }
    
	@EventHandler    
	public void onWorldLoaded(WorldLoadEvent evt) {
		 List<String> worlds = getConfig().getStringList("worlds");
		 for (String entry : worlds) {
				String[] split = entry.split(":");
				String world = split[0];
				final String cmd = split[1];
				if(world.equals(evt.getWorld().getName()))
				{
					getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

						public void run() {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(1));
						}
						}, 100L);
				}
			}
	}
}
