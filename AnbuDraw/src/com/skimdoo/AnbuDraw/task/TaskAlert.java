package com.skimdoo.AnbuDraw.task;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.skimdoo.AnbuDraw.Utilidades;

public class TaskAlert extends BukkitRunnable {
	private final String MESSAGE;
	private List<Player> sendTo;
	  
	public TaskAlert(String message, List<Player> players) {
	    this.MESSAGE = message;
	    this.sendTo = players;
	}
	  
	public void run() {
		for (Player p : this.sendTo) Utilidades.send(p, this.MESSAGE);
	}
	  
	public void removePlayer(Player p) {
	    if (this.sendTo.contains(p)) this.sendTo.remove(p);
	}
}