package com.skimdoo.AnbuDraw.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.skimdoo.AnbuDraw.game.Game;

public class TaskStart extends BukkitRunnable {
	Game b;
	public TaskStart(Game b) {
	    this.b = b;
	}
	public void run() {
	    this.b.start();
	}
}