package com.modcrafting.achievement.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.modcrafting.achievement.Achievement;

public class AchieveEntityListener implements Listener {
	private Achievement plugin;
	
	public AchieveEntityListener(Achievement plugin) {
        this.plugin = plugin;
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		if (!player.hasPermission("achievement.entitykill"))
			return;
		Entity entity = event.getEntity();
		String mobName = null;
		if(!(entity instanceof LivingEntity)) {
			return;
		}
		if(entity instanceof LivingEntity && !(event.getDamage() >= ((LivingEntity) entity).getHealth()))
			return;
		
		if(entity instanceof Player) {
			mobName = "player";
		} else {
			String doctored1 = entity.getClass().getName().toLowerCase().replace("org.bukkit.craftbukkit.entity.craft", "");
			String doctored2 = doctored1.replace("org.bukkit.entity.", "");
			mobName = doctored2;
		}
		plugin.db.registerKill(player, mobName);
		Integer kills = plugin.db.getKills(player, mobName);
		String configTest = "Kills." + mobName + "." + kills;
		Boolean achExists = plugin.reward.checkAchievement(configTest);
		if(achExists) {
			String msg = plugin.getConfig().getString(configTest + ".Message");
			msg=ChatColor.translateAlternateColorCodes('&', msg);
			if(msg.length() > 26) {
				plugin.interfaceSpout.sendAchievement(player, "Msg > 26 Chars", Material.DIAMOND_SWORD);
			} else {
				plugin.interfaceSpout.sendAchievement(player, msg, Material.DIAMOND_SWORD);
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
}