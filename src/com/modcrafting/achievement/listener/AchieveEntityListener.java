package com.modcrafting.achievement.listener;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.modcrafting.achievement.Achievement;

public class AchieveEntityListener extends EntityListener {
	
	private Achievement plugin;
	
	public AchieveEntityListener(Achievement plugin) {
        this.plugin = plugin;
    }
	
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		String mobName = null;
		if(!(entity instanceof LivingEntity)) {
			return;
		}
		if(entity instanceof LivingEntity) {
			LivingEntity alive = (LivingEntity) entity;
			if(!(event.getDamage() >= alive.getHealth())) {
				return;
			}
		}
		if(!(damager instanceof Player)) {
			return;
		}
		if(entity instanceof Player) {
			mobName = "player";
		} else {
			String doctored1 = entity.getClass().getName().toLowerCase().replace("org.bukkit.craftbukkit.entity.craft", "");
			String doctored2 = doctored1.replace("org.bukkit.entity.", "");
			mobName = doctored2;
		}
		Player player = (Player) damager;
		plugin.db.registerKill(player, mobName);
		Integer kills = plugin.db.getKills(player, mobName);
		String configTest = "Kills." + mobName + "." + kills;
		Boolean achExists = plugin.reward.checkAchievement(configTest);
		if(achExists) {
			String msg = config.getString(configTest + ".Message");
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
				Integer amount = config.getInt(configTest + ".Reward.Money.Amount", 0);
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
				Integer amount = config.getInt(configTest + ".Reward.Money.Amount", 0);
				plugin.reward.rewardMoney(player, amount);
			}
		}
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if(event instanceof EntityDamageByEntityEvent) {
			this.onEntityDamageByEntity((EntityDamageByEntityEvent) event);
			return;
		}
	}
}
