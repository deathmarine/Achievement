package com.modcrafting.achievement.event;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class AchievementListener extends CustomEventListener implements Listener {
	public void onAchievementGet(AchievementGetEvent event) {
		
	}
	public void onCustomEvent(Event event) {
		if(event instanceof AchievementGetEvent) {
			onAchievementGet((AchievementGetEvent) event);
		}
	}
}
