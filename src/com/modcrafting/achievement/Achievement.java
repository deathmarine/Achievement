package com.modcrafting.achievement;
import java.util.HashMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.modcrafting.achievement.db.SQLDatabases;
import com.modcrafting.achievement.listener.AchieveEntityListener;
import com.modcrafting.achievement.listener.AchieveBlockListener;
import com.modcrafting.achievement.listener.AchieveInventoryListener;
import com.modcrafting.achievement.spout.Interface;


public class Achievement extends JavaPlugin{
	public net.milkbowl.vault.economy.Economy economy = null;
	
	public AchieveBlockListener blockListener = new AchieveBlockListener(this);
	public AchieveEntityListener entityListener = new AchieveEntityListener(this);
	public AchieveInventoryListener inventoryListener = new AchieveInventoryListener(this);
	
	public Rewards reward = new Rewards(this);
	public Interface interfaceSpout = new Interface(this);
	public SQLDatabases db = new SQLDatabases();
	
	public HashMap<String, Integer> defBreaks = new HashMap<String, Integer>();
	public HashMap<String, Integer> defPlaces = new HashMap<String, Integer>();

	public void onDisable() {
		defBreaks.clear();
		defPlaces.clear();
	}
	
	public void onEnable() {
		this.getDataFolder().mkdir();
		this.saveDefaultConfig();
	    db.initialize(this);
		PluginManager pm = getServer().getPluginManager();
	    pm.registerEvents(blockListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(inventoryListener, this);		
	}
	
	public boolean setupEconomy(){
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}
}	