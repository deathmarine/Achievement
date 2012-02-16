package com.modcrafting.achievement.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AchievementListener implements Listener {
	public void onAchievementGet(AchievementGetEvent event){
	}
	@EventHandler
	public void onCustomEvent(Event event) {
		if(event instanceof AchievementGetEvent) {
			onAchievementGet((AchievementGetEvent) event);
		}
	}
}
