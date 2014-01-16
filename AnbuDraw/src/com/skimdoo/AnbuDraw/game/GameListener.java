package com.skimdoo.AnbuDraw.game;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import com.skimdoo.AnbuDraw.DrawMyThing;
import com.skimdoo.AnbuDraw.Utilidades;

public class GameListener implements Listener {
	private DrawMyThing instance;
	private DyeColor color = DyeColor.BLACK;
	
	public GameListener(DrawMyThing instance) {
	    this.instance = instance;
	}
	  
	@EventHandler
	public void onPlayerLogOut(PlayerQuitEvent event) {
		if (event.getPlayer().hasMetadata("inbmt")) {
			this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).leave(event.getPlayer());
	    }
	}
	  
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
	    if (event.getPlayer().hasMetadata("inbmt"))
	    {
	      if ((this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) != null) && 
	        (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder().getName() == event.getPlayer().getName()) && 
	        (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuildZone().contains(event.getBlock()))) {
	        event.getBlockPlaced().setType(event.getBlockPlaced().getType());
	      }
	      event.setCancelled(true);
	    }
	  }
	  
	  @SuppressWarnings("deprecation")
	@EventHandler
	  public void onPlayerInteract(PlayerInteractEvent event)
	  {
	    if (!event.getPlayer().hasMetadata("inbmt")) {
	      return;
	    }
	    if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) == null) {
	      return;
	    }
	    if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder() == null) {
	      return;
	    }
	    if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder().getName() != event.getPlayer().getName()) {
	      return;
	    }
	    if (event.getPlayer().getItemInHand() == null) {
	      return;
	    }
	    if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Pencil"))
	    {
	      if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.RIGHT_CLICK_AIR)) {
	        return;
	      }
	      Block b = event.getPlayer().getTargetBlock(null, 100);
	      if (b.getType() != Material.WOOL) {
	        return;
	      }
	      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIZZ, 1.0F, 1.0F);
	      b.setTypeIdAndData(Material.WOOL.getId(), this.color.getData(), true);
	      return;
	    }
	    if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Eraser"))
	    {
	      if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
	        return;
	      }
	      Block b = event.getPlayer().getTargetBlock(null, 100);
	      if (b.getType() != Material.WOOL) {
	        return;
	      }
	      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.0F);
	      b.setTypeIdAndData(Material.WOOL.getId(), DyeColor.WHITE.getData(), true);
	      return;
	    }
	    if (event.getPlayer().getItemInHand().getType() == Material.COMPASS)
	    {
	      if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
	        return;
	      }
	      this.instance.cci.clear();
	      this.instance.addCCIItems();
	      event.getPlayer().openInventory(this.instance.cci);
	      return;
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	  {
	    if ((event.getPlayer().hasMetadata("inbmt")) && 
	      (!event.getMessage().startsWith("/dmt")))
	    {
	    	Utilidades.send(event.getPlayer(), ChatColor.RED + "You cannot execute commands while ingame!");
	      event.setCancelled(true);
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerBreakBlock(BlockBreakEvent event)
	  {
	    if (event.getPlayer().hasMetadata("inbmt")) {
	      event.setCancelled(true);
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerDropItem(PlayerDropItemEvent event)
	  {
	    if (event.getPlayer().hasMetadata("inbmt")) {
	      event.setCancelled(true);
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerHit(EntityDamageEvent event)
	  {
	    if ((event.getEntity() instanceof Player))
	    {
	      Player p = (Player)event.getEntity();
	      if (p.hasMetadata("inbmt")) {
	        event.setCancelled(true);
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerHungerChange(FoodLevelChangeEvent event)
	  {
	    if (event.getEntity().hasMetadata("inbmt")) {
	      event.setCancelled(true);
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerChat(AsyncPlayerChatEvent event)
	  {
	    if ((event.getPlayer().hasMetadata("inbmt")) && 
	      (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) != null) && 
	      (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).isStarted())) {
	      if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder().getName() == event.getPlayer().getName())
	      {
	    	  Utilidades.send(event.getPlayer(), ChatColor.RED + "You cannot chat while you are a builder!");
	        event.setCancelled(true);
	      }
	      else
	      {
	        event.setCancelled(true);
	        String word = this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getWord();
	        if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).hasFound(event.getPlayer())) {
	          Utilidades.send(event.getPlayer(), ChatColor.RED + "You have already found the word!");
	        } else if (event.getMessage().toLowerCase().contains(word)) {
	          this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).wordFoundBy(event.getPlayer());
	        } else {
	          this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).sendMessage(ChatColor.BOLD + event.getPlayer().getName() + ": " + ChatColor.RESET + event.getMessage().toLowerCase());
	        }
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerMove(PlayerMoveEvent event)
	  {
	    if (event.getPlayer().hasMetadata("inbmt")) {
	      if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) != null) {
	        if (this.instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).isStarted()) {
	          if (event.getPlayer().getDisplayName().contains("[BUILDER]"))
	          {
	            if ((event.getTo().getX() == event.getFrom().getX()) && (event.getTo().getY() == event.getFrom().getY()) && (event.getTo().getZ() == event.getFrom().getZ())) {
	              return;
	            }
	            event.setTo(event.getFrom());
	          }
	        }
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onInvClick(InventoryClickEvent event)
	  {
	    Player p = (Player)event.getWhoClicked();
	    if (p.hasMetadata("inbmt")) {
	      if (event.getInventory().getName() == this.instance.cci.getName())
	      {
	        if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR)) {
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("White"))
	        {
	          setPencilColor(DyeColor.WHITE);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Black"))
	        {
	          setPencilColor(DyeColor.BLACK);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Red"))
	        {
	          setPencilColor(DyeColor.RED);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Orange"))
	        {
	          setPencilColor(DyeColor.ORANGE);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Yellow"))
	        {
	          setPencilColor(DyeColor.YELLOW);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Green"))
	        {
	          setPencilColor(DyeColor.LIME);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Blue"))
	        {
	          setPencilColor(DyeColor.LIGHT_BLUE);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Purple"))
	        {
	          setPencilColor(DyeColor.PURPLE);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	          return;
	        }
	        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Brown"))
	        {
	          setPencilColor(DyeColor.BROWN);
	          p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
	          p.closeInventory();
	        }
	      }
	      else
	      {
	        event.setCancelled(true);
	        p.closeInventory();
	        return;
	      }
	    }
	  }
	  
	  public void setPencilColor(DyeColor color)
	  {
	    this.color = color;
	  }
	  
	  public DyeColor getColor()
	  {
	    return this.color;
	  }
	}