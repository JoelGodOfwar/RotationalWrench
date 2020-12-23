package com.github.joelgodofwar.rw;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.joelgodofwar.rw.nms.RW_1_14_R1;
import com.github.joelgodofwar.rw.nms.RW_1_16_R2;
import com.github.joelgodofwar.rw.util.Ansi;
import com.github.joelgodofwar.rw.util.FileStuff;
import com.github.joelgodofwar.rw.util.Metrics;
import com.github.joelgodofwar.rw.util.StrUtils;
import com.github.joelgodofwar.rw.util.Tags;
import com.github.joelgodofwar.rw.util.UpdateChecker;
import com.github.joelgodofwar.rw.util.YmlConfiguration;
import com.google.common.collect.Lists;

public class RotationalWrench extends JavaPlugin implements Listener{
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static boolean UpdateCheck;
	public String UColdVers;
	public String UCnewVers;
	public String thisName = this.getName();
	public String thisVersion = this.getDescription().getVersion();
	public static boolean debug = false;
	public static String daLang;
	public YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static PluginDescriptionFile pdfFile;
	static String datafolder;
	boolean colorful_console;
	boolean UpdateAvailable =	false;
	
	
	private final String resourcePackUrl = "https://github.com/JoelGodOfwar/RotationalWrench/raw/main/resource/RWrench.zip";
    private final byte[] hash = new BigInteger("1ACF79C491B3CB9EEE50816AD0CC1FC45AABA147", 16).toByteArray();
    private final NamespacedKey RECIPE_KEY = new NamespacedKey(this, "rotational_wrench");
    @SuppressWarnings("unused")
	private final List<BlockFace> faces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);
    /**private final List<BlockFace> skullfaces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST
    		, BlockFace.NORTH_EAST, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST
    		, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST);*/
    public final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
    ShapedRecipe recipe;
    
    @Override // TODO: onEnable
	public void onEnable(){
    	UpdateCheck = getConfig().getBoolean("auto_update_check", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		oldconfig = new YamlConfiguration();
		pdfFile = this.getDescription();
		datafolder = this.getDataFolder().toString();
		colorful_console = getConfig().getBoolean("colorful_console", true);
		
    	PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(ChatColor.GREEN + "**************************************" + ChatColor.RESET);
		logger.info(ChatColor.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + ChatColor.RESET + " Loading...");
		
		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			log("jarfile contains dev, debug set to true.");
		}
		
    	/**	Check for config */
		try{
			if(!getDataFolder().exists()){
				log("Data Folder doesn't exist");
				log("Creating Data Folder");
				getDataFolder().mkdirs();
				log("Data Folder Created at " + getDataFolder());
			}
			File file = new File(getDataFolder(), "config.yml");
			if(debug){logDebug("" + file);}
			if(!file.exists()){
				log("config.yml not found, creating!");
				saveResource("config.yml", true);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/** Check if config.yml is up to date.*/
		boolean needConfigUpdate = false;
		//String oldConfig = new File(getDataFolder(), "config.yml").getPath().toString();
		try {
			oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
		} catch (Exception e2) {
			logWarn("Could not load config.yml");
			e2.printStackTrace();
		}
		String checkconfigversion = oldconfig.getString("version", "1.0.0");
		if(checkconfigversion != null){
			if(!checkconfigversion.equalsIgnoreCase("1.0.12")){
				needConfigUpdate = true;
			}
		}
		if(needConfigUpdate){
			try {
				FileStuff.copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
			} catch (IOException e) {
				e.printStackTrace();
			}
			saveResource("config.yml", true);
			try {
				config.load(new File(getDataFolder(), "config.yml"));
			} catch (IOException | InvalidConfigurationException e1) {
				logWarn("Could not load config.yml");
				e1.printStackTrace();
			}
			try {
				oldconfig.load(new File(getDataFolder(), "old_config.yml"));
			} catch (IOException | InvalidConfigurationException e1) {
				e1.printStackTrace();
			}
			config.set("auto_update_check", oldconfig.get("auto_update_check", true));
			config.set("debug", oldconfig.get("debug", false));
			config.set("lang", oldconfig.get("lang", "en_US"));
			config.set("colorful_console", oldconfig.get("colorful_console", true));
			config.set("blacklist", oldconfig.get("blacklist", ""));
			config.set("spamfilter", oldconfig.get("spamfilter", true));
			
			config.set("enabled.anvil", oldconfig.get("enabled.anvil", true));
			config.set("enabled.armorstands", oldconfig.get("enabled.armorstands", true));
			config.set("enabled.banner", oldconfig.get("enabled.banner", true));
			config.set("enabled.basalt", oldconfig.get("enabled.basalt", true));
			config.set("enabled.beds", oldconfig.get("enabled.beds", true));
			config.set("enabled.bee", oldconfig.get("enabled.bee", true));
			config.set("enabled.bell", oldconfig.get("enabled.bell", true));
			config.set("enabled.bone", oldconfig.get("enabled.bone", true));
			config.set("enabled.buttons", oldconfig.get("enabled.buttons", true));
			config.set("enabled.campfires", oldconfig.get("enabled.campfires", true));
			config.set("enabled.carvedpumpkin", oldconfig.get("enabled.carvedpumpkin", true));
			config.set("enabled.chain", oldconfig.get("enabled.chain", true));
			config.set("enabled.chests", oldconfig.get("enabled.chests", true));
			config.set("enabled.coral", oldconfig.get("enabled.coral", true));
			config.set("enabled.doors", oldconfig.get("enabled.doors", true));
			config.set("enabled.endrod", oldconfig.get("enabled.endrod", true));
			config.set("enabled.fencegates", oldconfig.get("enabled.fencegates", true));
			config.set("enabled.heads", oldconfig.get("enabled.heads", true));
			config.set("enabled.ladder", oldconfig.get("enabled.ladder", true));
			config.set("enabled.lantern", oldconfig.get("enabled.lantern", true));
			config.set("enabled.logs", oldconfig.get("enabled.logs", true));
			config.set("enabled.pillars", oldconfig.get("enabled.pillars", true));
			config.set("enabled.rails", oldconfig.get("enabled.rails", true));
			config.set("enabled.redstone", oldconfig.get("enabled.redstone", true));
			config.set("enabled.shulker", oldconfig.get("enabled.shulker", true));
			config.set("enabled.signs", oldconfig.get("enabled.signs", true));
			config.set("enabled.slabs", oldconfig.get("enabled.slabs", true));
			config.set("enabled.stairs.rotate", oldconfig.get("enabled.stairs.rotate", true));
			config.set("enabled.stairs.invert", oldconfig.get("enabled.stairs.invert", true));
			config.set("enabled.terracotta", oldconfig.get("enabled.terracotta", true));
			config.set("enabled.torch", oldconfig.get("enabled.torch", true));
			config.set("enabled.trapdoors.rotate", oldconfig.get("enabled.trapdoors.rotate", true));
			config.set("enabled.trapdoors.invert", oldconfig.get("enabled.trapdoors.invert", true));
			config.set("enabled.workstations", oldconfig.get("enabled.workstations", true));

			try {
				config.save(new File(getDataFolder(), "config.yml"));
				config.load(new File(getDataFolder(), "config.yml"));
			} catch (IOException | InvalidConfigurationException e) {
				logWarn("Could not save old settings to config.yml");
				e.printStackTrace();
			}
			log("config.yml has been updated");
		}else{
			//log("" + "not found");
		}
		/** End Config update check */
		
		/** Update Checker */
		if(UpdateCheck){
			try {
				Bukkit.getConsoleSender().sendMessage("Checking for updates...");
				UpdateChecker updater = new UpdateChecker(this, 85119);
				if(updater.checkForUpdates()) {
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();
					Bukkit.getConsoleSender().sendMessage(this.getName() + Ansi.AnsiColor("RED", colorful_console) + " v" + UColdVers + ChatColor.RESET + " New version available! " + Ansi.AnsiColor("GREEN", colorful_console) + " v" + UCnewVers + ChatColor.RESET);
					Bukkit.getConsoleSender().sendMessage(UpdateChecker.getResourceUrl());
				}else{
					UpdateAvailable = false;
				}
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
			}
		}
		/** end update checker */
		try{
	    	ItemMeta meta = Objects.requireNonNull(wrench.getItemMeta());
	        meta.setDisplayName(ChatColor.RESET + "Rotational Wrench");
	        meta.setUnbreakable(true);
	        meta.setCustomModelData(4321);
	        wrench.setItemMeta(meta);
	        recipe = new ShapedRecipe(RECIPE_KEY, wrench)
	                .shape(" g "," gg","i  ")
	                .setIngredient('g', Material.GOLD_INGOT)
	                .setIngredient('i', Material.IRON_INGOT);
	        Bukkit.addRecipe(recipe);
	        log("Rotational Wrench Recipe added.");
		}catch(Exception e){}
        
        String packageName = this.getServer().getClass().getPackage().getName();
    	String version = packageName.substring(packageName.lastIndexOf('.') + 2);
    	if(debug)logDebug("version=" + version);
    	if( version.contains("1_16_R") ){
    		getServer().getPluginManager().registerEvents( new RW_1_16_R2(this), this);
		}else if( version.contains("1_15_R1") || version.contains("1_14_R1")){
			getServer().getPluginManager().registerEvents( new RW_1_14_R1(this), this);
		}else{
			logWarn("Not compatible with this version of Minecraft:" + version);
			getServer().getPluginManager().disablePlugin(this);
		}
    	//getServer().getPluginManager().registerEvents(RW_EventHandler.getHandler(version, this), this);
        getServer().getPluginManager().registerEvents(this, this);
        log("Events Registered.");
        
        consoleInfo("enabled");
        
        try {
			//PluginBase plugin = this;
			Metrics metrics  = new Metrics(this);
			// New chart here
			// myPlugins()
			metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> call() throws Exception {
					Map<String, Integer> valueMap = new HashMap<>();
					if(getServer().getPluginManager().getPlugin("DragonDropElytra") != null){valueMap.put("DragonDropElytra", 1);}
					if(getServer().getPluginManager().getPlugin("NoEndermanGrief") != null){valueMap.put("NoEndermanGrief", 1);}
					if(getServer().getPluginManager().getPlugin("PortalHelper") != null){valueMap.put("PortalHelper", 1);}
					if(getServer().getPluginManager().getPlugin("ShulkerRespawner") != null){valueMap.put("ShulkerRespawner", 1);}
					if(getServer().getPluginManager().getPlugin("MoreMobHeads") != null){valueMap.put("MoreMobHeads", 1);}
					if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){valueMap.put("SilenceMobs", 1);}
					if(getServer().getPluginManager().getPlugin("VillagerWorkstationHighlights") != null){valueMap.put("VillagerWorkstationHighlights", 1);}
					if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
					return valueMap;
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("auto_update_check", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("auto_update_check").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_debug", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("debug").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_lang", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("lang").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("colorful_console", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("colorful_console").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("redstone", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.redstone").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("terracotta", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.terracotta").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("stairs_rotate", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.stairs.rotate").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("stairs_invert", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.stairs.invert").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("slabs", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.slabs").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("armorstands", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.armorstands").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("rails", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.rails").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("beds", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.beds").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("chain", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.chain").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("trapdoors_rotate", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.trapdoors.rotate").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("trapdoors_invert", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.trapdoors.invert").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("carvedpumpkin", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.carvedpumpkin").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("chests", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.chests").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("fencegates", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.fencegates").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("doors", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.doors").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("workstations", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.workstations").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("endrod", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.endrod").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("logs", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.logs").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("heads", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.heads").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("bell", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enabled.bell").toUpperCase();
				}
			}));
		}catch (Exception e){
			// Failed to submit the stats
		}
    }
    
    @Override // TODO: onDisable
	public void onDisable(){
		/** Experimental Code */
		Bukkit.removeRecipe(RECIPE_KEY);
		Bukkit.clearRecipes();
		/** Experimental Code */
		
		consoleInfo("disabled");
	}
    
    public void consoleInfo(String state) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(ChatColor.GREEN + "**************************************" + ChatColor.RESET);
		logger.info(ChatColor.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + ChatColor.RESET + " is " + state);
		logger.info(ChatColor.GREEN + "**************************************" + ChatColor.RESET);
	}
	
	public	void log(String dalog){// TODO: log
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(ChatColor.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + ChatColor.RESET + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + dalog);
	}
	public void logWarn(String dalog){
		log(ChatColor.RED + "[WARN] " + ChatColor.RESET  + dalog);
	}
    
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event){
	    Player player = event.getPlayer();
	    boolean onblacklist = false;
        onblacklist = StrUtils.stringContains(config.getString("blacklist", ""),event.getPlayer().getWorld().getName());
        if(!onblacklist){
        	player.discoverRecipe(RECIPE_KEY);
        }
	    
	    if(player.isOp() && UpdateCheck){	
			
		}
		//if(p.isOp() && UpdateCheck||p.hasPermission("sps.showUpdateAvailable")){	
		/** Notify Ops */
		if(UpdateAvailable&&(player.isOp()||player.hasPermission("rotationalwrench.showUpdateAvailable"))){
			player.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " v" + UColdVers + ChatColor.RESET + " New version available! " + ChatColor.GREEN + " v" + UCnewVers + ChatColor.RESET + "\n" + ChatColor.GREEN + UpdateChecker.getResourceUrl() + ChatColor.RESET);
		}

		if(player.getName().equals("JoelYahwehOfWar")||player.getName().equals("JoelGodOfWar")){
			player.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
			//p.sendMessage("seed=" + p.getWorld().getSeed());
		}
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event){
		if(event.getRecipe().equals(wrench)){
			boolean onblacklist = false;
	        onblacklist = StrUtils.stringContains(config.getString("blacklist", ""),event.getInventory().getLocation().getWorld().getName());
	        if(onblacklist){
	        	event.setCancelled(true);
	        	return;
	        }
			Player player = (Player) event.getWhoClicked();
			player.setResourcePack(resourcePackUrl, hash);
		}
	}
	
	
	
	public Block findBed(Location loc){
		Block block1 = loc.add(1, 0, 0).getBlock();
		Block block2 = loc.subtract(2, 0, 0).getBlock();
		Block block3 = loc.add(1, 0, 1).getBlock();
		Block block4 = loc.subtract(0, 0, 2).getBlock();
		if(Tags.BEDS.isTagged(block1.getType())){
			return block1;
		}else if(Tags.BEDS.isTagged(block2.getType())){
			return block2;
		}else if(Tags.BEDS.isTagged(block3.getType())){
			return block3;
		}else if(Tags.BEDS.isTagged(block4.getType())){
			return block4;
		}
		return loc.getBlock();
		//.getBlockData()).getPart()
	}
	
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		if (cmd.getName().equalsIgnoreCase("RWrench")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
				sender.sendMessage(ChatColor.RESET + " /RWrench - Shows this.");
				sender.sendMessage(ChatColor.RESET + " /RWrench Texture - Prompt to download Wrench texture.");
				if(sender.hasPermission("rotationalwrench.reload")){
					sender.sendMessage(ChatColor.RESET + " /RWrench Reload - Reloads the plugin.");
				}
				if(sender.hasPermission("rotationalwrench.toggledebug")){
					sender.sendMessage(ChatColor.RESET + " /RWrench ToggleDebug/TD - Toggles Debug true/false.");
				}
				if(sender.hasPermission("rotationalwrench.showUpdateAvailable")){
					sender.sendMessage(ChatColor.RESET + " /RWrench Update - Checks if there is an update.");
				}
				sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("reload")){
			if( sender.isOp() || sender.hasPermission("rotationalwrench.reload") || !(sender instanceof Player) ){
				getServer().getPluginManager().disablePlugin(this);
				getServer().getPluginManager().enablePlugin(this);
				UpdateCheck = getConfig().getBoolean("auto_update_check", true);
				debug = getConfig().getBoolean("debug", false);
				daLang = getConfig().getString("lang", "en_US");
				oldconfig = new YamlConfiguration();
				pdfFile = this.getDescription();
				datafolder = this.getDataFolder().toString();
				colorful_console = getConfig().getBoolean("colorful_console", true);
				sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RESET + " has been reloaded.");
			}
		}
		if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
			if( sender.isOp() || sender.hasPermission("rotationalwrench.toggledebug") || !(sender instanceof Player) ){
				debug = !debug;
				sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RESET + " debug has been set to " + debug);
				return true;
			}else if(!sender.hasPermission("rotationalwrench.toggledebug")){
				sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " You do not have permission to do this.");
				return false;
			}
		}
		if(args[0].equalsIgnoreCase("texture")){
			if(player != null){
				player.setResourcePack(resourcePackUrl, hash);
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("update")){ // TODO: Command update
			// Player must be OP and auto-update-check must be true
		//if(sender.isOp() && UpdateCheck||sender.hasPermission("sps.op") && UpdateCheck||sender.hasPermission("sps.*") && UpdateCheck){	
			if((sender.isOp()||sender.hasPermission("rotationalwrench.showUpdateAvailable"))){
			    
				BukkitTask updateTask = this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {

					public void run() {
						try {
							Bukkit.getConsoleSender().sendMessage("Checking for updates...");
							UpdateChecker updater = new UpdateChecker(thisVersion, 68139);
							if(updater.checkForUpdates()) {
								UpdateAvailable = true;
								UColdVers = updater.oldVersion();
								UCnewVers = updater.newVersion();
								sender.sendMessage(ChatColor.YELLOW + thisName + ChatColor.RED + " v" + UColdVers + ChatColor.RESET + " New version available! " + ChatColor.GREEN + " v" + UCnewVers + ChatColor.RESET);
								sender.sendMessage(UpdateChecker.getResourceUrl());
								
							}else{
								sender.sendMessage(ChatColor.YELLOW + thisName + ChatColor.RED + " v" + thisVersion + ChatColor.RESET + " Up to date." + ChatColor.RESET);
								UpdateAvailable = false;
							}
						}catch(Exception e) {
							sender.sendMessage(ChatColor.RED + "Could not process update check");
							Bukkit.getConsoleSender().sendMessage(Ansi.AnsiColor("RED", colorful_console) + "Could not process update check");
							e.printStackTrace();
						}
					}
					
				});
							
				return true;	
			}else{
				sender.sendMessage(ChatColor.YELLOW + this.getName() + " You are not OP, or auto-update-check is set to false in config.yml");
				return false;
			}
		}
		return false;
	}
	
	public boolean isDebug(){
		return debug;
	}
	
	public boolean getBoolean(String string, boolean bool){
		return getConfig().getBoolean(string, bool);
	}
}
