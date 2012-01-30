package com.modcrafting.achievement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.modcrafting.achievement.db.SQLDatabases;
import com.modcrafting.achievement.listener.AchieveEntityListener;
import com.modcrafting.achievement.listener.AchieveBlockListener;
import com.modcrafting.achievement.listener.AchieveInventoryListener;
import com.modcrafting.achievement.spout.Interface;


public class Achievement extends JavaPlugin{
	public final static Logger log = Logger.getLogger("Minecraft");
	public net.milkbowl.vault.permission.Permission permission = null;
	public net.milkbowl.vault.economy.Economy economy = null;
	public org.getspout.spoutapi.player.SpoutPlayer spout = null;
	public String maindir = "plugins/Achievement/";
	public Interface interfaceSpout = new Interface(this);
	public AchieveBlockListener blockListener = new AchieveBlockListener(this);
	public AchieveEntityListener entityListener = new AchieveEntityListener(this);
	public AchieveInventoryListener inventoryListener = null;
	public Rewards reward = new Rewards(this);
	public SQLDatabases db = new SQLDatabases();
	public HashMap<String, Integer> defBreaks = new HashMap<String, Integer>();
	public HashMap<String, Integer> defPlaces = new HashMap<String, Integer>();

	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		defBreaks.clear();
		defPlaces.clear();
		log.log(Level.INFO,"[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is disabled!" );
		}
	
	protected void createDefaultConfiguration(String name) {
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {

			InputStream input =
				this.getClass().getResourceAsStream("/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					log.log(Level.INFO, getDescription().getName()
							+ ": Default configuration file written: " + name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		}
	}
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		new File(maindir).mkdir();
		createDefaultConfiguration("config.yml");
	    /*config.set("Places.stone.1.Message",(String) "Your first stone smelt!");
	    config.set("Breaks.stone.1.Message",(String) "Your first cobble!");
	    config.set("Kills.zombie.1.Message",(String) "Your first zombie!");
	    config.set("Kills.zombie.1.Reward.Money.Amount",(int) 1);
	    config.set("Kills.zombie.1.Reward.Item.Type",(String) "stone");
	    config.set("Kills.zombie.1.Reward.Item.Amount",(int) 1);
	    config.set("Crafts.torch.1.Message",(String) "Light it up!");
	    config.set("Crafts.torch.1.Reward.Money.Amount",(int) 5);
	    this.saveConfig();*/
	    db.initialize(this);
	    //Register events:
		PluginManager pm = getServer().getPluginManager();
	    pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
		if(setupSpout()) pm.registerEvent(Type.CUSTOM_EVENT, inventoryListener, Priority.Normal, this);
		log.log(Level.INFO,"[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is enabled!" );
		
		
	}
	public Boolean setupPermissions()
    {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	public boolean setupEconomy(){
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
				return (economy != null);
		}	
	public boolean setupSpout(){
		RegisteredServiceProvider<org.getspout.spoutapi.player.SpoutPlayer> spoutProvider = getServer().getServicesManager().getRegistration(org.getspout.spoutapi.player.SpoutPlayer.class);
			if (spoutProvider != null) {
				spout = spoutProvider.getProvider();
				inventoryListener = new AchieveInventoryListener(this);
			}
				return (spout != null);
		}
}	