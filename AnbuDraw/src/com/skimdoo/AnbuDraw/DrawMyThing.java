package com.skimdoo.AnbuDraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.skimdoo.AnbuDraw.cuboid.CuboidZone;
import com.skimdoo.AnbuDraw.game.Game;
import com.skimdoo.AnbuDraw.game.GameListener;
import com.skimdoo.AnbuDraw.sign.SignListener;

public class DrawMyThing extends JavaPlugin {
	private List<Game> games;
	public Logger logger;
	public DrawMyThing plugin;
	public Inventory cci;
	public static final List<String> DEFAULT_WORDS = new ArrayList<String>(Arrays.asList(new String[] { "manzana", "pelota", "espacio", "vaso", "leche", "papas", "peces", "manelucos", "anbu", "futbol", "montaña", "simpson", "M12", "matar" }));
	private List<String> words;
	private ItemStack white;
	private ItemStack black;
	private ItemStack red;
	private ItemStack orange;
	private ItemStack yellow;
	private ItemStack green;
	private ItemStack blue;
	private ItemStack purple;
	private ItemStack brown;
  
	public void onDisable() {
		List<String> names = new ArrayList<String>();
		for (Game b : this.games) {
			names.add(b.getName());
			b.stop();
			b.save(getConfig());
		}
		getConfig().set("games", names);
		this.games.clear();
		saveConfig();
		getLogger().info("Se ha deshabilitado correctamente");
	}
  
	public void onEnable() {
		loadConfig();
		this.cci = Bukkit.createInventory(null, 9, "Pinceles");
		GameListener bListener = new GameListener(this);
		SignListener sListener = new SignListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(bListener, this);
		pm.registerEvents(sListener, this);
		getLogger().info("Se ha habilitado correctamente");
	}
  
	public String getRandomWord() {
		int i = this.words.size();
		Random r = new Random();
		return (String)this.words.get(r.nextInt(i));
	}
  
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equals("ad")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				if (args.length > 0) {
					if ((args[0].equals("papel1")) && (player.hasPermission("dmt.admin"))) {
						Utilidades.send(player, ChatColor.GREEN + "¡Posición §f1 §apara el papel establecido!");
						player.setMetadata("bmtp1", new FixedMetadataValue(this, Utilidades.LocationToString(player.getLocation())));
						return true;
					}
					if ((args[0].equals("papel2")) && (player.hasPermission("dmt.admin"))) {
						Utilidades.send(player, ChatColor.GREEN + "¡Posición §f2 §apara el papel establecido!");
						player.setMetadata("bmtp2", new FixedMetadataValue(this, Utilidades.LocationToString(player.getLocation())));
						return true;
					}
					if ((args[0].equals("spawn")) && (player.hasPermission("dmt.admin"))) {
						Utilidades.send(player, ChatColor.GREEN + "¡Punto §fspawn general §apara la arena establecido!");
						player.setMetadata("bmtspec", new FixedMetadataValue(this, Utilidades.LocationToString(player.getLocation())));
						return true;
					}
					if ((args[0].equals("spawnpintor")) && (player.hasPermission("dmt.admin"))) {
						Utilidades.send(player, ChatColor.GREEN + "¡Punto §fspawn del pintor §apara la arena establecido!");
						player.setMetadata("bmtbspawn", new FixedMetadataValue(this, Utilidades.LocationToString(player.getLocation())));
						return true;
					}
					if ((args[0].equals("crear")) && (player.hasPermission("dmt.admin"))) {
						if (args.length > 1) {
							if ((player.hasMetadata("bmtp1")) && (player.hasMetadata("bmtp2")) && (player.hasMetadata("bmtspec"))) {
								if (getGameByName(args[1]) != null) {
									Utilidades.send(player, "§c¡Una arena ya contiene el mismo nombre!");
								} else {
									Location loc1 = Utilidades.StringToLoc(((MetadataValue)player.getMetadata("bmtp1").get(0)).asString());
									Location loc2 = Utilidades.StringToLoc(((MetadataValue)player.getMetadata("bmtp2").get(0)).asString());
									Location spawn = Utilidades.StringToLoc(((MetadataValue)player.getMetadata("bmtspec").get(0)).asString());
									Location bspawn = Utilidades.StringToLoc(((MetadataValue)player.getMetadata("bmtbspawn").get(0)).asString());
									Game b = new Game(new CuboidZone(loc1.getBlock(), loc2.getBlock()), spawn, bspawn, args[1], this);
									if ((args.length > 2) && (isInteger(args[2]))) {
										b.setMaxPlayers(Integer.parseInt(args[2]));
									}
									this.games.add(b);
									Utilidades.send(player, ChatColor.GREEN + "La arena " + args[1] + " ha sido creada.");
								}
							} else {
								Utilidades.send(player, ChatColor.RED + "No se ha podido crear la arena.");
							}
						} else {
							Utilidades.send(player, ChatColor.RED + "¡Especifica el nombre de la arena!");
						}
					} else if ((args[0].equals("eliminar")) && (player.hasPermission("dmt.admin"))) {
						if (args.length > 1) {
							if (getGameByName(args[1]) != null) {
								getGameByName(args[1]).remove(getConfig());
								this.games.remove(getGameByName(args[1]));
								Utilidades.send(player, ChatColor.GREEN + "La arena " + args[1] + " ha sido eliminada.");
							} else {
								Utilidades.send(player, ChatColor.RED + "La arena " + args[1] + " no existe.");
							}
						} else {
							Utilidades.send(player, ChatColor.RED + "¡Especifica el nombre de la arena!");
						}
					} else if ((args[0].equals("max")) && (player.hasPermission("dmt.admin"))) {
						if (args.length > 1) {
							if (getGameByName(args[1]) != null) {
								if (args.length > 2) {
									getGameByName(args[1]).stop();
									getGameByName(args[1]).setMaxPlayers(Integer.valueOf(args[2]).intValue());
									Utilidades.send(player, ChatColor.GREEN + "La arena " + args[1] + " ahora tiene como límite de jugadores " + args[2] + ".");
								}
							} else {
								Utilidades.send(player, ChatColor.RED + "La arena " + args[1] + " no existe.");
							}
						} else {
							Utilidades.send(player, ChatColor.RED + "¡Especifica el nombre de la arena!");
						}
					} else if ((args[0].equals("min")) && (player.hasPermission("dmt.admin"))) {
						if (args.length > 1) {
							if (getGameByName(args[1]) != null) {
								if (args.length > 2) {
									getGameByName(args[1]).stop();
									getGameByName(args[1]).setMinPlayers(Integer.valueOf(args[2]).intValue());
									Utilidades.send(player, ChatColor.GREEN + "La arena " + args[1] + " ahora necesita a " + args[2] + " usuarios para iniciar.");
								}
							} else {
								Utilidades.send(player, ChatColor.RED + "La arena " + args[1] + " no existe.");
							}
						} else {
							Utilidades.send(player, ChatColor.RED + "¡Especifica el nombre de la arena!");
						}
					} else if ((args[0].equals("reload")) && (player.hasPermission("dmt.admin"))) {
						stopAllGames();
						reloadConfig();
						loadConfig();
					} else if ((args[0].equals("entrar")) && (player.hasPermission("dmt.default"))) {
						if (player.hasMetadata("inbmt")) Utilidades.send(player, ChatColor.RED + "¡Ya estas en una arena!");
						else if (args.length > 1) {
							if (getGameByName(args[1]) != null) getGameByName(args[1]).join(player);
							else Utilidades.send(player, ChatColor.RED + "La arena " + args[1] + " no existe.");
						} else {
							Utilidades.send(player, ChatColor.RED + "¡Especifica el nombre de la arena!");
						}
					} else if ((args[0].equals("salir")) && (player.hasPermission("dmt.default"))) {
						if (player.hasMetadata("inbmt")) getGameByName(((MetadataValue)player.getMetadata("inbmt").get(0)).asString()).leave(player);
						else Utilidades.send(player, ChatColor.RED + "¡No estas en una arena!");
					} else if (args[0].equals("help")) {
						if (player.hasPermission("dmt.admin")) {
							player.sendMessage(ChatColor.BLUE + "/ad papel1 " + ChatColor.GRAY + "Set the first point to your current position");
							player.sendMessage(ChatColor.BLUE + "/ad papel2 " + ChatColor.GRAY + "Set the second point to your current position");
							player.sendMessage(ChatColor.BLUE + "/ad spawn " + ChatColor.GRAY + "Set the spawn point to your current position");
							player.sendMessage(ChatColor.BLUE + "/ad spawnpintor " + ChatColor.GRAY + "Set the spawn point to your current position");
							player.sendMessage(ChatColor.BLUE + "/ad crear [arena] " + ChatColor.GRAY + "Create a new game with the specified name");
							player.sendMessage(ChatColor.BLUE + "/ad eliminar [arena] " + ChatColor.GRAY + "Remove the game with the specified name");
							player.sendMessage(ChatColor.BLUE + "/ad max [arena] [num]" + ChatColor.GRAY + "Change player limit of the game wih the specified name");
							player.sendMessage(ChatColor.BLUE + "/ad min [arena] [num]" + ChatColor.GRAY + "Change min players to start the game of the specified name");
							player.sendMessage(ChatColor.BLUE + "/ad reload" + ChatColor.GRAY + "Reload the plugin");
						}
						if (player.hasPermission("dmt.default")) {
							player.sendMessage(ChatColor.BLUE + "/dmt entrar [arena] " + ChatColor.GRAY + "Join the game with the specified name");
							player.sendMessage(ChatColor.BLUE + "/dmt salir [arena] " + ChatColor.GRAY + "Leave your current game");
						}
					} else {
						Utilidades.send(player, ChatColor.RED + "Propiedad desconocida, usa /dmt help");
					}
				} else {
					Utilidades.send(player, ChatColor.RED + "Uso: /dmt [propiedad]");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Solo usuarios pueden ejecutar dicho comando");
			}
		}
		return false;
	}
  
	private void stopAllGames() {
		for (Game b : this.games) b.stop();
	}
  
	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		getConfig().options().header("\n ANBU DRAW, adaptación de DrawMyThing, hecho por mjl1010.");
		if (!getConfig().contains("Palabras")) getConfig().set("Palabras", DEFAULT_WORDS);
		saveConfig();
		this.games.clear();
		this.words.clear();
		if ((getConfig().getList("Arenas") != null) && (getConfig().getList("Arenas").size() > 0) && ((getConfig().getList("Arenas").get(0) instanceof String))) {
			List<String> games = getConfig().getStringList("Arenas");
			for (String s : games) this.games.add(Game.load(getConfig(), s, this));
		}
		if ((getConfig().getList("Palabras") != null) && (getConfig().getList("Palabras").size() > 0) && ((getConfig().getList("Palabras").get(0) instanceof String))) {
			List<String> words = getConfig().getStringList("Palabras");
			for (String s : words) {
				if (s != null) this.words.add(s);
			}
		}
    }
  
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
  
	public Game getGameByName(String name) {
		Game result = null;
		for (Game b : this.games) {
			if (b.getName().equals(name)) result = b;
		}
		return result;
	}
  
	@SuppressWarnings("deprecation")
	public DrawMyThing() {
		this.games = new ArrayList<Game>();
		this.logger = Logger.getLogger("Minecraft");
		this.plugin = this;
		this.words = new ArrayList<String>();
		
		this.white = new ItemStack(Material.WOOL);
		ItemMeta wm = this.white.getItemMeta();
		wm.setDisplayName(ChatColor.WHITE + "Blanco");
		this.white.setItemMeta(wm);
		
		this.black = new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getData());
		ItemMeta bm = this.black.getItemMeta();
		bm.setDisplayName(ChatColor.BLACK + "Negro");
		this.black.setItemMeta(bm);
		
		this.red = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
		ItemMeta rm = this.red.getItemMeta();
		rm.setDisplayName(ChatColor.RED + "Rojo");
		this.red.setItemMeta(rm);
		
		this.orange = new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData());
		ItemMeta om = this.orange.getItemMeta();
		om.setDisplayName(ChatColor.GOLD + "Naranja");
		this.orange.setItemMeta(om);
		
		this.yellow = new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData());
		ItemMeta ym = this.yellow.getItemMeta();
		ym.setDisplayName(ChatColor.YELLOW + "Amarillo");
		this.yellow.setItemMeta(ym);
		
		this.green = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData());
		ItemMeta gm = this.green.getItemMeta();
		gm.setDisplayName(ChatColor.GREEN + "Verde");
		this.green.setItemMeta(gm);
		
		this.blue = new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData());
		ItemMeta blm = this.blue.getItemMeta();
		blm.setDisplayName(ChatColor.AQUA + "Azul");
		this.blue.setItemMeta(blm);
		
		this.purple = new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getData());
		ItemMeta pm = this.purple.getItemMeta();
		pm.setDisplayName(ChatColor.DARK_PURPLE + "Violeta");
		this.purple.setItemMeta(pm);
		
		this.brown = new ItemStack(Material.WOOL, 1, DyeColor.BROWN.getData());
		ItemMeta brm = this.brown.getItemMeta();
		brm.setDisplayName(ChatColor.GRAY + "Marrón");
		this.brown.setItemMeta(brm);
	}
  
	public void addCCIItems() {
		this.cci.addItem(new ItemStack[] { this.white });
		this.cci.addItem(new ItemStack[] { this.black });
		this.cci.addItem(new ItemStack[] { this.red });
		this.cci.addItem(new ItemStack[] { this.orange });
		this.cci.addItem(new ItemStack[] { this.yellow });
		this.cci.addItem(new ItemStack[] { this.green });
		this.cci.addItem(new ItemStack[] { this.blue });
		this.cci.addItem(new ItemStack[] { this.purple });
		this.cci.addItem(new ItemStack[] { this.brown });
	}
}
