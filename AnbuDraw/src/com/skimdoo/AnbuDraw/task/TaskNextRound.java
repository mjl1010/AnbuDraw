package com.skimdoo.AnbuDraw.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.skimdoo.AnbuDraw.game.Game;

public class TaskNextRound extends BukkitRunnable {
	private final Game buildZone;
	public TaskNextRound(Game buildZone) {
	    this.buildZone = buildZone;
	}
	public void run() {
	    this.buildZone.startRound();
	}
}