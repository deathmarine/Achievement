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
			if (plugin.setupSpout()){
				plugin.spout = (SpoutPlayer) player;
				plugin.spout.sendNotification("Achievement Get!", msg, mat);
			} else {
				if(player instanceof SpoutPlayer && ((SpoutPlayer) player).isSpoutCraftEnabled()){
					plugin.spout = (SpoutPlayer) player;
					plugin.spout.sendNotification("Achievement Get!", msg, mat);
				}else{
					player.sendMessage(ChatColor.YELLOW + "Achievement Get!");
					player.sendMessage(ChatColor.WHITE + msg);
				}
			}
		}
}
	

