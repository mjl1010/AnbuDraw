package com.skimdoo.AnbuDraw.game;

import com.skimdoo.AnbuDraw.Utilidades;
import com.skimdoo.AnbuDraw.DrawMyThing;
import com.skimdoo.AnbuDraw.cuboid.CuboidZone;
import com.skimdoo.AnbuDraw.task.TaskAlert;
import com.skimdoo.AnbuDraw.task.TaskNextRound;
import com.skimdoo.AnbuDraw.task.TaskStart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Game implements Listener {
	private DrawMyThing instance;
	private CuboidZone buildzone;
	private Location spawn;
	private Location bspawn;
	private Map<Player, Integer> score = new HashMap<Player, Integer>();
	private Map<Player, Boolean> ready = new HashMap<Player, Boolean>();
	private Map<Player, Integer> hasBeenBuilder = new HashMap<Player, Integer>();
	private Map<Player, ItemStack[]> inventario = new HashMap<Player, ItemStack[]>();
	private Map<Player, ItemStack[]> armor = new HashMap<Player, ItemStack[]>();
	private Map<Player, GameMode> gamemode = new HashMap<Player, GameMode>();
	private Map<Player, Integer> foodLevel = new HashMap<Player, Integer>();
	private List<Player> hasFound = new ArrayList<Player>();
	private Player builder;
	private int players;
	private int minplayers = 2;
	private int maxplayers = 12;
	private int buildPerPlayer = 2;
	private String name;
	private String word;
	private boolean started;
	private boolean wordHasBeenFound = false;
  	private int playerFound = 0;
  	private List<BukkitRunnable> tasks = new ArrayList<BukkitRunnable>();
  	private boolean acceptWords = true;
  	private List<Block> signs = new ArrayList<Block>();
  	ScoreboardManager manager = Bukkit.getScoreboardManager();
  	Scoreboard board = this.manager.getNewScoreboard();
  	Objective objective;
  	public ItemStack pencil = new ItemStack(Material.STICK);
  	public ItemStack eraser;
  	public ItemStack colorpicker;
  	
  	public Game(CuboidZone build, Location spawn, Location bspawn, String name, DrawMyThing instance) {
  		ItemMeta pm = this.pencil.getItemMeta();
  		pm.setDisplayName(ChatColor.GREEN + "Pincel");
  		this.pencil.setItemMeta(pm);

  		this.eraser = new ItemStack(Material.ARROW);
  		ItemMeta em = this.eraser.getItemMeta();
  		em.setDisplayName(ChatColor.RED + "Borrador");
  		this.eraser.setItemMeta(em);
    
  		this.colorpicker = new ItemStack(Material.COMPASS);
  		ItemMeta ccm = this.colorpicker.getItemMeta();
  		ccm.setDisplayName(ChatColor.GOLD + "Paleta de Colores");
  		this.colorpicker.setItemMeta(ccm);
    
  		this.buildzone = build;
  		this.spawn = spawn;
  		this.bspawn = bspawn;
  		this.name = name;
  		this.instance = instance;
  		this.objective = this.board.registerNewObjective(this.name + "_points", "dummy");
  		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
  		this.objective.setDisplayName(ChatColor.BLUE + "[ANBU DRAW] " + ChatColor.GOLD + "Puntos");
  	}
  
  	public void cancelTasks() {
  		for (BukkitRunnable r : this.tasks) r.cancel();
  	}
  
  	public void leave(Player player) {
  		if (this.score.containsKey(player)) {
  			if ((!this.started) && (this.players == this.maxplayers)) cancelTasks();
  		}
  		player.setFoodLevel(((Integer)this.foodLevel.get(player)).intValue());
  		this.foodLevel.remove(player);
  		this.score.remove(player);
  		this.ready.remove(player);
  		this.hasBeenBuilder.remove(player);
  		if (hasFound(player)) this.playerFound -= 1;
  		this.hasFound.remove(player);
  		if (this.inventario.get(player) != null) player.getInventory().setContents((ItemStack[])this.inventario.get(player));
  		this.inventario.remove(player);
  		if (this.armor.get(player) != null) player.getInventory().setContents((ItemStack[])this.armor.get(player));
  		this.armor.remove(player);
  		player.setAllowFlight(false);
  		player.setDisplayName(player.getName());
  		player.setGameMode((GameMode)this.gamemode.get(player));
  		this.gamemode.remove(player);
  		this.board.resetScores(player);
  		player.setScoreboard(this.manager.getMainScoreboard());
  		this.players -= 1;
  		player.teleport(Utilidades.StringToLoc(((MetadataValue)player.getMetadata("oldLoc").get(0)).asString()));
  		player.removeMetadata("oldLoc", this.instance);
  		player.removeMetadata("inbmt", this.instance);
  		updateSigns();
  		if (this.players > 1) sendMessage(ChatColor.BLUE + "El usuario §f" + player.getName() + " §9ha salido de la arena.");
  		else if (isStarted()) {
  			cancelTasks();
  			stop();
  		}
  	}
  
  	public void join(Player player) {
  		if (!isStarted()) {
  			if (!this.score.containsKey(player)) {
  				player.setMetadata("oldLoc", new FixedMetadataValue(this.instance, Utilidades.LocationToString(player.getLocation())));
  				player.teleport(this.spawn);
  				if (this.players < this.maxplayers) {
  					ItemStack[] inventory = player.getInventory().getContents();
  					ItemStack[] saveInventory = new ItemStack[inventory.length];
  					for (int i = 0; i < inventory.length; i++) {
  						if (inventory[i] != null) {
  							saveInventory[i] = inventory[i].clone();
  						}
  					}
  					this.inventario.put(player, saveInventory);
  					this.foodLevel.put(player, Integer.valueOf(player.getFoodLevel()));
  					player.setFoodLevel(20);
  					player.setDisplayName(player.getName());
  					this.gamemode.put(player, player.getGameMode());
  					player.getInventory().clear();
  					player.setGameMode(GameMode.ADVENTURE);
  					player.setMetadata("inbmt", new FixedMetadataValue(this.instance, getName()));
  					player.setScoreboard(this.board);
  					this.score.put(player, Integer.valueOf(0));
  					this.ready.put(player, Boolean.valueOf(false));
  					this.players += 1;
  					if (this.players > 0) {
  						for (Player p : this.score.keySet()) {
  							Utilidades.send(p, ChatColor.BLUE + "¡El usuario §f" + player.getName() + " §9ha entrado a la arena! §7(§f" + String.valueOf(this.players) + "§7/§f" + String.valueOf(new StringBuilder(String.valueOf(this.maxplayers)).append("§7)").toString()));
  						}
  					}
  					if (this.players >= this.minplayers) {
  						sendMessage(ChatColor.GREEN + "¡El juego empieza en 5 segundos!");
  						TaskStart start = new TaskStart(this);
  						start.runTaskLater(this.instance, 100L);
  						this.tasks.add(start);
  					}
  					updateSigns();
  				} else {
  					Utilidades.send(player, ChatColor.RED + "¡La arena esta a tope!");
  				}
  			}
  		} else {
  			Utilidades.send(player, ChatColor.RED + "¡El juego ha empezado!");
  		}
  	}
  
  	private String getNewWord() {
  		return this.instance.getRandomWord();
  	}
  
  	public void start() {
  		if (!this.started) {
  			cancelTasks();
  			this.started = true;
  			this.word = null;
  			if (this.players < 3) {
  				this.buildPerPlayer = 3;
  			}
  			this.hasBeenBuilder.clear();
  			for (Player p : this.score.keySet()) {
  				this.hasBeenBuilder.put(p, Integer.valueOf(0));
  			}
  			startRound();
  			updateSigns();
  		}
  	}
  
  	public void startRound() {
  		if (this.word != null) {
  			sendMessage(ChatColor.GOLD + "¡Atención! " + ChatColor.BOLD + "La palabra es: §f§l" + this.word);
  		}
  		cancelTasks();
  		this.wordHasBeenFound = false;
  		this.playerFound = 0;
  		this.hasFound.clear();
  		this.acceptWords = true;
  		this.word = getNewWord();
  		this.buildzone.clear();
  		this.buildzone.setWool(DyeColor.WHITE);
  		getNextBuilder();
  		TaskAlert alert1 = new TaskAlert(ChatColor.GOLD + "¡30 segundos de expiración de la ronda!", getPlayers());
  		TaskAlert alert2 = new TaskAlert(ChatColor.GOLD + "¡10 segundos de expiración de la ronda!", getPlayers());
  		TaskNextRound endRound = new TaskNextRound(this);
  		endRound.runTaskLater(this.instance, 1500L);
  		this.tasks.add(endRound);
  		TaskAlert endRoundMsg = new TaskAlert(ChatColor.GOLD + "¡Fuera de tiempo! Siguiente ronda en 5 segundos.", getPlayers());
  		endRoundMsg.runTaskLater(this.instance, 1400L);
  		alert1.runTaskLater(this.instance, 600L);
  		alert2.runTaskLater(this.instance, 1200L);
  		this.tasks.add(alert1);
  		this.tasks.add(alert2);
  		this.tasks.add(endRoundMsg);
  	}
  
  	public void removePlayerFromAlerts(Player p) {
  		for (BukkitRunnable task : this.tasks) {
  			if (task instanceof TaskAlert){
  				TaskAlert taskAlert = (TaskAlert)task;
  				taskAlert.removePlayer(p);
  			}
  		}
  	}
  
  	public List<Player> getPlayers() {
  		List<Player> result = new ArrayList<Player>();
  		for (Player p : this.score.keySet()) {
  			result.add(p);
  		}
  		return result;
  	}
  
  	public void stop() {
  		this.started = false;
  		this.buildzone.clear();
  		this.buildzone.setWool(DyeColor.WHITE);
  		List<Player> toKick = new ArrayList<Player>();
  		for (Player p : this.score.keySet()) {
  			toKick.add(p);
  		}
  		for (Player p : toKick) {
  			leave(p);
  		}
  	}
  
  	private void getNextBuilder() {
  		if (getBuilder() != null) {
  			this.builder.setGameMode(GameMode.ADVENTURE);
  			this.builder.setFlying(false);
  			this.builder.teleport(this.spawn);
  			this.builder.setDisplayName(this.builder.getName());
  			this.builder.getInventory().clear();
  			this.builder = null;
  		}
  		for (int i = 0; i < this.buildPerPlayer; i++) {
  			for (Player p : this.hasBeenBuilder.keySet()) {
  				if (((Integer)this.hasBeenBuilder.get(p)).intValue() <= i) {
  					setBuilder(p);
  					return;
  				}
  			}
  		}
  		sendMessage(ChatColor.RED + "¡Se acabó el juego!");
  		Player winner = null;
  		for (Player p : this.score.keySet()) {
  			if (winner != null) {
  				if (((Integer)this.score.get(p)).intValue() > ((Integer)this.score.get(winner)).intValue()) {
  					winner = p;
  				}
  			} else {
  				winner = p;
  			}
  		}
  		if (this.score.containsKey(winner)) {
  			sendMessage(ChatColor.GREEN + "Puntos:");
  			for (Player p : this.score.keySet()) {
  				sendMessage(ChatColor.AQUA + p.getName() + ":" + ChatColor.YELLOW + " " + String.valueOf(this.score.get(p)));
  			}
  			sendMessage(ChatColor.GOLD + "§lGanador: §f§l" + winner.getName());
  		}
  		stop();
  	}
  
  	private void setBuilder(Player p) {
  		this.builder = p;
  		this.hasBeenBuilder.put(p, Integer.valueOf(((Integer)this.hasBeenBuilder.get(p)).intValue() + 1));
  		p.teleport(this.bspawn);
  		p.setGameMode(GameMode.CREATIVE);
  		p.setFlying(true);
    	p.getInventory().addItem(new ItemStack[] { this.pencil });
    	p.getInventory().addItem(new ItemStack[] { this.eraser });
    	p.getInventory().addItem(new ItemStack[] { this.colorpicker });
    	p.setDisplayName(ChatColor.RED + "[BUILDER] " + p.getName());
    	sendMessage("§9¡Ahora le toca a §f" + p.getName() + " §9dibujar!");
    	Utilidades.send(p, ChatColor.GOLD + "¡Atención! " + ChatColor.BOLD + "La palabra es: §f§l" + this.word);
 	}
  
  	public void sendMessage(String message) {
  		for (Player p : this.score.keySet()) {
  			Utilidades.send(p, message);
  		}
  	}
  
  	public boolean isStarted() {
  		return this.started;
  	}
  
  	public String getName() {
  		return this.name;
  	}
  
  	public void save(FileConfiguration file) {
  		file.set("games" + getName() + ".pos1", Utilidades.LocationToString(this.buildzone.getCorner1().getLocation()));
  		file.set("games" + getName() + ".pos2", Utilidades.LocationToString(this.buildzone.getCorner2().getLocation()));
  		file.set("games" + getName() + ".spawn", Utilidades.LocationToString(this.spawn));
  		file.set("games" + getName() + ".bspawn", Utilidades.LocationToString(this.bspawn));
  		file.set("games" + getName() + ".bspawn", Utilidades.LocationToString(this.bspawn));
  		file.set("games" + getName() + ".minplayers", Integer.valueOf(this.minplayers));
  		file.set("games" + getName() + ".maxplayers", Integer.valueOf(this.maxplayers));
  		List<String> signData = new ArrayList<String>();
  		for (Block s : this.signs) {
  			if (s.getType() == Material.WALL_SIGN) {
  				String loc = Utilidades.LocationToString(s.getLocation());
  				String display;
  				if (s.hasMetadata("display")) {
  					display = ";" + ((MetadataValue)s.getMetadata("display").get(0)).asString();
  				} else {
  					display = ";none";
  				}
  				String result = loc + display;
  				signData.add(result);
  			}
  		}
  		file.set("games" + getName() + ".signs", signData);
  	}
  
  	public void remove(FileConfiguration file) {
  		stop();
  		file.set(getName(), null);
  		this.instance.saveConfig();
  	}
  
  	public static Game load(FileConfiguration file, String name, DrawMyThing instance) {
  		Location corner1 = Utilidades.StringToLoc(file.getString("games" + name + ".pos1"));
  		Location corner2 = Utilidades.StringToLoc(file.getString("games" + name + ".pos2"));
  		Location spawn = Utilidades.StringToLoc(file.getString("games" + name + ".spawn"));
  		Location bspawn = Utilidades.StringToLoc(file.getString("games" + name + ".bspawn"));
  		Game b = new Game(new CuboidZone(corner1.getBlock(), corner2.getBlock()), spawn, bspawn, name, instance);
  		b.setMinPlayers(file.getInt("games" + name + ".minplayers"));
  		b.setMaxPlayers(file.getInt("games" + name + ".maxplayers"));
  		if (file.getList("games" + name + ".signs") != null) {
  			List<String> signLoc = file.getStringList("games" + name + ".signs");
  			for (String s : signLoc) {
  				String loc = s.split(";")[0];
  				String display = s.split(";")[1];
  				Location l = Utilidades.StringToLoc(loc);
  				Block block = l.getBlock();
  				if (((block.getState() instanceof Sign)) && (block.getType() == Material.WALL_SIGN)) {
  					b.registerSign(block, display);
  				}
  			}
  		}
  		return b;
  	}
  	
  	public void setMinPlayers(int i) {
  		this.minplayers = i;
  	}
  
  	public void setMaxPlayers(int i) {
  		this.maxplayers = i;
  	}
  
  	public Player getBuilder() {
  		return this.builder;
  	}
  
  	public CuboidZone getBuildZone() {
  		return this.buildzone;
  	}
  
  	public String getWord() {
  		return this.word;
  	}
  
  	public void increaseScore(Player p, int value) {
  		if (this.score.containsKey(p)) {
  			this.score.put(p, Integer.valueOf(((Integer)this.score.get(p)).intValue() + value));
  			Score scoreBoard = this.objective.getScore(p);
  			scoreBoard.setScore(((Integer)this.score.get(p)).intValue());
  		}
  	}
  
 	public void wordFoundBy(Player player) {
 		if ((this.acceptWords) && (!this.hasFound.contains(player))) {
 			this.hasFound.add(player);
 			for (Player p : this.score.keySet()) {
 				p.getWorld().playSound(p.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
 			}
 			if (!this.wordHasBeenFound) {
 				sendMessage("§a¡El usuario §f" + player.getName() + " §aha acertado la palabra primero! §f§l+3");
 				this.builder.sendMessage("§aNo esta mal, alguien la ha acertado: §f§l+2");
 				increaseScore(player, 3);
 				increaseScore(this.builder, 2);
 				this.wordHasBeenFound = true;
 			} else {
 				sendMessage("§7¡El usuario §f" + player.getName() + " §7ha acertado la palabra! §f§l+1");
 				increaseScore(player, 1);
 			}
 			this.playerFound += 1;
 		}
 		if (this.playerFound == this.players - 1) {
 			sendMessage(ChatColor.GREEN + "¡La próxima ronda empieza en 5 segundos!");
 			cancelTasks();
 			TaskNextRound endRound = new TaskNextRound(this);
 			endRound.runTaskLater(this.instance, 100L);
 			this.tasks.add(endRound);
 		}
  	}
  
 	public void setNotAcceptWords() {
 		this.acceptWords = false;
  	}
  
 	public int getMinPlayers() {
 		return this.minplayers;
  	}
  
 	public int getMaxPlayers() {
 		return this.maxplayers;
  	}
  
 	public boolean hasFound(Player player) {
 		return this.hasFound.contains(player);
 	}
  
 	public void updateSigns() {
 		for (Block b : this.signs) {
 			if ((b.getState() instanceof Sign)) {
 				Sign s = (Sign)b.getState();
 				s.setLine(0, ChatColor.BLUE + "[ANBU DRAW]");
 				s.setLine(1, ChatColor.GOLD + getName());
 				s.setLine(2, "§8(§7" + String.valueOf(this.players) + "§8/§7" + this.maxplayers + "§8)");
 				s.setLine(3, ChatColor.GREEN + "¡Juega!");
 				s.update();
 			}
 		}
 	}
  
 	public void registerSign(Block block) {
 		registerSign(block, "none");
 	}
  
 	public void registerSign(Block block, String display) {
 		if ((block.getState() instanceof Sign)) {
 			this.signs.add(block);
 			block.setMetadata("bmtjoinsign", new FixedMetadataValue(this.instance, getName()));
 			if (display.equalsIgnoreCase("wool")) {
 				block.setMetadata("display", new FixedMetadataValue(this.instance, "wool"));
 			} else if (display.equalsIgnoreCase("lamp")) {
 				block.setMetadata("display", new FixedMetadataValue(this.instance, "lamp"));
 			} else if (display.equalsIgnoreCase("kany")) {
 				block.setMetadata("display", new FixedMetadataValue(this.instance, "kany"));
 			}
 			updateSigns();
 		}
 	}
  
 	public void removeSign(Block block) {
 		if ((block.getState() instanceof Sign)) {
 			this.signs.remove(block);
 			if (block.hasMetadata("bmtjoinsign")) {
 				block.removeMetadata("bmtjoinsign", this.instance);
 			}
 		}
 	}
}