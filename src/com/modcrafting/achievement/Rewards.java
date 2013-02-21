package com.modcrafting.achievement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rewards {
	Achievement plugin;

	public Rewards(Achievement achievement) {
        this.plugin = achievement;
    }
	
	public Boolean checkAchievement(String ach) {
		String check = plugin.getConfig().getString(ach + ".Message", "null");
		if(check.equals("null")) {
			return false;
		} else {
			return true;
		}
	}
	
	public String reward(String ach) {
		String check = ach + ".Reward.Money";
		Integer money = plugin.getConfig().getInt(check + ".Amount", 0);
		Integer amount = plugin.getConfig().getInt(ach + ".Reward.Item.Amount", 0);
		if(amount == 0 && !(money == 0)) {
			return "money";
		}
		if(money == 0 && !(amount == 0)) {
			return "item";
		}
		if(money == 0 && amount == 0) {
			return "none";
		}
		if(!(money == 0) && !(amount == 0)) {
			return "both";
		}
		return "none";
	}
	
	public ItemStack getItemReward(Player player, String ach) {
		ItemStack item = new ItemStack(0, 0);
		String check = ach + ".Reward.Item.";
		String name = plugin.getConfig().getString(check + "Type", "stone");
		Integer amount = plugin.getConfig().getInt(check +  "Amount", 0);
		item.setAmount(amount);
		item.setType(Material.getMaterial(name.toUpperCase()));
		return item;
	}
	
	public void rewardMoney(Player player, Integer amount) {
		if(plugin.setupEconomy()){
			String pname = player.getName();
			String price = Integer.toString(amount);
			double amtd = Double.valueOf(price.trim());
			plugin.economy.bankDeposit(pname, amtd);
		}
	}

}
