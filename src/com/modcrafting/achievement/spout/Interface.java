package com.modcrafting.achievement.spout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.player.SpoutPlayer;

import com.modcrafting.achievement.Achievement;

public class Interface {
	Achievement plugin;
	public Interface(Achievement achievement) {
		this.plugin = achievement;
	}

	public void sendAchievement(Player player, String msg, Material mat) {
		SpoutPlayer cp = (SpoutPlayer) player;
		if(cp.isSpoutCraftEnabled()) {
			cp.sendNotification("Achievement Get!", msg, mat);
		} else {
			cp.sendMessage(ChatColor.YELLOW + "Achievement Get!");
			cp.sendMessage(ChatColor.WHITE + msg);
		}
	}

}
