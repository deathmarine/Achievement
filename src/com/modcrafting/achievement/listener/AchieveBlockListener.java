package com.modcrafting.achievement.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.modcrafting.achievement.Achievement;

public class AchieveBlockListener implements Listener {
	Achievement plugin;

	public AchieveBlockListener(Achievement instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(!player.hasPermission("achievement.blockbreak"))
			return;
		Block block = event.getBlock();
		String blockName = block.getType().name().toLowerCase();
		plugin.db.registerBreak(player, block);
		Integer breaks = plugin.db.getBlockBreaks(player, block);
		String configTest = "Breaks." + blockName + "." + breaks;
		Boolean achExists = plugin.reward.checkAchievement(configTest);
		if(achExists) {
			String msg = plugin.getConfig().getString("Breaks." + blockName + "." + breaks + ".Message");
			msg=ChatColor.translateAlternateColorCodes('&', msg);
			if(msg.length() > 26) {
			plugin.interfaceSpout.sendAchievement(player, "Your msg > 26 chars", block.getType());
				plugin.getLogger().info( "Your msg must be less than 26 characters!");
			} else {
				plugin.interfaceSpout.sendAchievement(player, msg, block.getType());
			}
			String reward = plugin.reward.reward(configTest);
			if(reward.equals("none")) {
				return;
			}
			if(reward.equals("money")) {
				Integer amount = plugin.getConfig().getInt(configTest + ".Reward.Money.Amount", 0);
				plugin.reward.rewardMoney(player, amount);
			}
			if(reward.equals("item")) {
				ItemStack item = plugin.reward.getItemReward(player, configTest);
				PlayerInventory inv = player.getInventory();
				inv.addItem(item);
			}
			if(reward.equals("both")) {
				ItemStack item = plugin.reward.getItemReward(player, configTest);
				PlayerInventory inv = player.getInventory();
				inv.addItem(item);
				Integer amount = plugin.getConfig().getInt(configTest + ".Reward.Money.Amount", 0);
				plugin.reward.rewardMoney(player, amount);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("achievement.blockplace"))
			return;
		Block block = event.getBlock();
		String blockName = block.getType().name().toLowerCase();
		plugin.db.registerPlace(player, block);
		Integer places = plugin.db.getBlockPlaces(player, block);
		String configTest = "Places." + blockName + "." + places;
		Boolean achExists = plugin.reward.checkAchievement(configTest);
		if(achExists) {
			String msg = plugin.getConfig().getString("Places." + blockName + "." + places + ".Message");
			if(msg.length() > 26) {
				plugin.interfaceSpout.sendAchievement(player, "Your msg > 26 chars", block.getType());
				plugin.getLogger().info("Your msg must be less than 26 characters!");
			} else {
				plugin.interfaceSpout.sendAchievement(player, msg, block.getType());
			}
		}
		String reward = plugin.reward.reward(configTest);
		if(reward.equals("none")) {
			return;
		}
		if(reward.equals("money")) {
			Integer amount = plugin.getConfig().getInt(configTest + ".Reward.Money.Amount", 0);
			plugin.reward.rewardMoney(player, amount);
		}
		if(reward.equals("item")) {
			ItemStack item = plugin.reward.getItemReward(player, configTest);
			PlayerInventory inv = player.getInventory();
			inv.addItem(item);
		}
		if(reward.equals("both")) {
			ItemStack item = plugin.reward.getItemReward(player, configTest);
			PlayerInventory inv = player.getInventory();
			inv.addItem(item);
			Integer amount = plugin.getConfig().getInt(configTest + ".Reward.Money.Amount", 0);
			plugin.reward.rewardMoney(player, amount);
		}
	}
}
