package com.modcrafting.achievement.spout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.modcrafting.achievement.Achievement;

public class Interface {
	Achievement plugin;
	
	public Interface(Achievement achievement) {
		this.plugin = achievement;
	}

	public void sendAchievement(Player player, String msg, Material mat) {
		try {
			Class.forName("org.getspout.spoutapi.player.SpoutPlayer");
			if (player instanceof org.getspout.spoutapi.player.SpoutPlayer && 
				((org.getspout.spoutapi.player.SpoutPlayer) player).isSpoutCraftEnabled()){
					((org.getspout.spoutapi.player.SpoutPlayer) player).sendNotification("Achievement Get!", msg, mat);
			}
		} catch (ClassNotFoundException e) {
			player.sendMessage(ChatColor.YELLOW + "Achievement Get!");
			player.sendMessage(ChatColor.WHITE + msg);
		}
	}
}
	

