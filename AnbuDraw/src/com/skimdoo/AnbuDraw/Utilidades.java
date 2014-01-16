package com.skimdoo.AnbuDraw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utilidades {
	private static final String PREFIX = ChatColor.BLUE + "[ANBU DRAW] " + ChatColor.WHITE;
	public static void send(Player p, String message) {
	    p.sendMessage(PREFIX + message);
	}
	public static void broadcast(String message) {
	    Bukkit.broadcastMessage(PREFIX + message);
	}
	public static String LocationToString(Location l) {
	    return String.valueOf(new StringBuilder(String.valueOf(l.getWorld().getName())).append(":").append(l.getBlockX()).toString()) + ":" + String.valueOf(l.getBlockY()) + ":" + String.valueOf(l.getBlockZ());
	}
	public static Location StringToLoc(String s) {
	    Location l = null;
	    try {
	    	World world = Bukkit.getWorld(s.split(":")[0]);
	    	Double x = Double.valueOf(Double.parseDouble(s.split(":")[1]));
	    	Double y = Double.valueOf(Double.parseDouble(s.split(":")[2]));
	    	Double z = Double.valueOf(Double.parseDouble(s.split(":")[3]));
	    	l = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue());
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	    return l;
	}
}