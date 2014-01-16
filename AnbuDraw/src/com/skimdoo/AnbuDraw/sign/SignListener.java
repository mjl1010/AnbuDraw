package com.skimdoo.AnbuDraw.sign;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;

import com.skimdoo.AnbuDraw.DrawMyThing;
import com.skimdoo.AnbuDraw.Utilidades;
import com.skimdoo.AnbuDraw.task.TaskRegisterSign;

public class SignListener implements Listener {
	private final DrawMyThing plugin;
	
	public SignListener(DrawMyThing plugin) {
	    this.plugin = plugin;
	}
	  
	@EventHandler
	public void onBlockPlaced(SignChangeEvent Event) {
	    if (Event.getLine(0).startsWith("[anbuentrar]")) {
	    	if ((Event.getLine(1).length() > 0) && (Event.getPlayer().hasPermission("dmt.admin"))) {
	    		String line2 = Event.getLine(1);
	    		if (Event.getBlock().getType() == Material.WALL_SIGN) {
	    			if (this.plugin.getGameByName(line2) != null) {
	    				if (Event.getLine(2) != null) {
	    					new TaskRegisterSign(Event.getBlock(), this.plugin.getGameByName(line2), Event.getLine(2)).runTaskLater(this.plugin, 20L);
	    				} else {
	    					new TaskRegisterSign(Event.getBlock(), this.plugin.getGameByName(line2), "none").runTaskLater(this.plugin, 20L);
	    				}
	    				Utilidades.send(Event.getPlayer(), "¡Cartel creado correctamente!");
	    			} else {
	    				Utilidades.send(Event.getPlayer(), "Arena desconocida: " + line2);
	    			}
	    		}
	    	}
	    } else if ((Event.getLine(0).startsWith("[anbusalir]")) && (Event.getPlayer().hasPermission("dmt.admin"))) {
	    	Event.setLine(0, ChatColor.BLUE + "[ANBU DRAW]");
	    	Event.setLine(1, "");
	    	Event.setLine(2, ChatColor.RED + "¡Salir!");
	    	Event.setLine(3, "");
	    }
	}
	  
	@EventHandler
	public void onBlockBroken(BlockBreakEvent Event) {
		Block b = Event.getBlock();
	    if (((b.getState() instanceof Sign)) && (b.hasMetadata("bmtjoinsign"))) {
	    	if (Event.getPlayer().hasPermission("dmt.admin")) {
	    		String name = ((MetadataValue)b.getMetadata("bmtjoinsign").get(0)).asString();
	    		if (this.plugin.getGameByName(name) != null) {
	    			this.plugin.getGameByName(name).removeSign(b);
	    			Utilidades.send(Event.getPlayer(), ChatColor.RED + "¡Cartel eliminado!");
	    		}
	    	} else {
	    		Event.setCancelled(true);
	    	}
	    }
	}
	  
	@EventHandler
	public void onInteract(PlayerInteractEvent Event) {
		if ((Event.getClickedBlock() != null) && (Event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			Player player = Event.getPlayer();
			Block block = Event.getClickedBlock();
			if ((block.getState() instanceof Sign)) {
				Sign sign = (Sign)block.getState();
				if (block.hasMetadata("bmtjoinsign")) {
					String game = ((MetadataValue)sign.getMetadata("bmtjoinsign").get(0)).asString();
					if ((this.plugin.getGameByName(game) != null) && (player.hasPermission("dmt.default"))) {
						this.plugin.getGameByName(game).join(player);
					}
				} else if ((sign.getLine(0).equals(ChatColor.BLUE + "[ANBU DRAW]")) && (sign.getLine(2).equals(ChatColor.RED + "¡Salir!"))) {
					if ((player.hasPermission("dmt.default")) && (player.hasMetadata("inbmt")) && (this.plugin.getGameByName(((MetadataValue)player.getMetadata("inbmt").get(0)).asString()) != null)) {
						this.plugin.getGameByName(((MetadataValue)player.getMetadata("inbmt").get(0)).asString()).leave(player);
					}
				}
			}
	    }
	}
}