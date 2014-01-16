package com.skimdoo.AnbuDraw.task;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.skimdoo.AnbuDraw.game.Game;

public class TaskRegisterSign extends BukkitRunnable {
	private Block b;
	private Game bz;
	private String display;
	  
	public TaskRegisterSign(Block b, Game bz, String display) {
	    this.b = b;
	    this.bz = bz;
	    this.display = display;
	}
	  
	public void run() {
	    this.bz.registerSign(this.b, this.display);
	}
}