package com.skimdoo.AnbuDraw.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.skimdoo.AnbuDraw.game.Game;

public class TaskStopWords extends BukkitRunnable {
	private Game buildzone;
	public void run() {
	    this.buildzone.setNotAcceptWords();
	}
}