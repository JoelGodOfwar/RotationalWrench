package com.github.joelgodofwar.rw;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.joelgodofwar.rw.i18n.Translator;
import com.github.joelgodofwar.rw.nms.RW_1_14_R1;
import com.github.joelgodofwar.rw.nms.RW_1_16_R2;
import com.github.joelgodofwar.rw.nms.RW_1_17_R1;
import com.github.joelgodofwar.rw.nms.RW_1_19_R1;
import com.github.joelgodofwar.rw.nms.RW_1_20_R1;
import com.github.joelgodofwar.rw.util.FileStuff;
import com.github.joelgodofwar.rw.util.Metrics;
import com.github.joelgodofwar.rw.util.StrUtils;
import com.github.joelgodofwar.rw.util.Tags;
import com.github.joelgodofwar.rw.util.Utils;
import com.github.joelgodofwar.rw.util.VersionChecker;
import com.github.joelgodofwar.rw.util.YmlConfiguration;
import com.google.common.collect.Lists;

public class RotationalWrench extends JavaPlugin implements Listener{
	/** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	public final static Logger logger = Logger.getLogger("Minecraft");
	static String THIS_NAME;
	static String THIS_VERSION;
	/** update checker variables */
	public int projectID = 85119; // https://spigotmc.org/resources/71236
	public String githubURL = "https://github.com/JoelGodOfwar/RotationalWrench/raw/main/versioncheck/1.14/versions.xml";
	boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public static boolean UpdateCheck;
    public String DownloadLink = "https://www.spigotmc.org/resources/rotationalwrench.85119";
	/** end update checker variables */
	public static boolean debug = false;
	public static String daLang;
	public YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static String datafolder;
	boolean colorful_console;
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
	String pluginName = THIS_NAME;
	Translator lang2;
	private Set<String> triggeredPlayers = new HashSet<>();
    
    @Override // TODO: onEnable
	public void onEnable(){
    	long startTime = System.currentTimeMillis();
    	UpdateCheck = getConfig().getBoolean("auto_update_check", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		oldconfig = new YamlConfiguration();
		datafolder = this.getDataFolder().toString();
		colorful_console = getConfig().getBoolean("colorful_console", true);
		lang2 = new Translator(daLang, getDataFolder().toString());
		THIS_NAME = this.getDescription().getName();
		THIS_VERSION = this.getDescription().getVersion();
		if(!getConfig().getBoolean("console.longpluginname", true)) {
			pluginName = "RW";
		}else {
			pluginName = THIS_NAME;
		}
		
    	
		logger.info(ChatColor.GREEN + "**************************************" + ChatColor.RESET);
		logger.info(ChatColor.YELLOW + THIS_NAME + " v" + THIS_VERSION + ChatColor.RESET + " Loading...");
		
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
			if(!checkconfigversion.equalsIgnoreCase("1.0.16")){
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
			config.set("enabled.lightning_rod", oldconfig.get("enabled.lightning_rod", true));
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
			config.set("enabled.walls", oldconfig.get("enabled.walls", true));
			config.set("enabled.workstations", oldconfig.get("enabled.workstations", true));

			config.set("wrench_recipe.shape", oldconfig.get("wrench_recipe.shape", new String[]{" g ", " gg", "i  "}));
			config.set("wrench_recipe.ingredients", oldconfig.get("wrench_recipe.ingredients", new String[]{"g:GOLD_INGOT", "i:IRON_INGOT"}));
			config.set("wrench_recipe.unbreakable", oldconfig.get("wrench_recipe.unbreakable", true));
			config.set("wrench_recipe.translation", oldconfig.get("wrench_recipe.translation", "Rotational Wrench"));

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
				VersionChecker updater = new VersionChecker(this, projectID, githubURL);
				if(updater.checkForUpdates()) {
					/** Update available */
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();
					
					log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					log("* " + get("rw.version.message").toString().replace("<MyPlugin>", THIS_NAME) );
					log("* " + get("rw.version.old_vers") + ChatColor.RED + UColdVers );
					log("* " + get("rw.version.new_vers") + ChatColor.GREEN + UCnewVers );
					log("*");
					log("* " + get("rw.version.please_update") );
					log("*");
					log("* " + get("rw.version.download") + ": " + DownloadLink + "/history");
					log("* " + get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
					log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				}else{
					/** Up to date */
					log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					log("* " + get("rw.version.curvers"));
					log("* " + get("rw.version.donate") + ": https://ko-fi.com/joelgodofwar");
					log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					UpdateAvailable = false;
				}
			}catch(Exception e) {
				/** Error */
				log(get("rw.version.update.error"));
				e.printStackTrace();
			}
		}else {
			/** auto_update_check is false so nag. */
			log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
			log("* " + get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
			log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
		}
		/** end update checker */
		
		loadRecipe();
        
        String packageName = this.getServer().getClass().getPackage().getName();
    	String version = packageName.substring(packageName.lastIndexOf('.') + 2);
    	if(debug)logDebug("version=" + version);
    	if( version.contains("1_20_R") ){
			getServer().getPluginManager().registerEvents( new RW_1_20_R1(this), this);
    	}else if( version.contains("1_19_R") ){
			getServer().getPluginManager().registerEvents( new RW_1_19_R1(this), this);
    	}else if( version.contains("1_17_R1") || version.contains("1_18_R1")  || version.contains("1_18_R2") ){
			getServer().getPluginManager().registerEvents( new RW_1_17_R1(this), this);
		}else if( version.contains("1_16_R") ){
    		getServer().getPluginManager().registerEvents( new RW_1_16_R2(this), this);
		}else  if( version.contains("1_15_R1") || version.contains("1_14_R1")){
			getServer().getPluginManager().registerEvents( new RW_1_14_R1(this), this);
		}else{
			logWarn("Not compatible with this version of Minecraft: " + version);
			getServer().getPluginManager().disablePlugin(this);
		}
    	//getServer().getPluginManager().registerEvents(RW_EventHandler.getHandler(version, this), this);
        getServer().getPluginManager().registerEvents(this, this);
        log("Events Registered.");
        
        consoleInfo("ENABLED - Loading took " + LoadTime(startTime));
        
        try {
			//PluginBase plugin = this;
			Metrics metrics  = new Metrics(this, 9190);
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
		    		if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
					if(getServer().getPluginManager().getPlugin("VillagerWorkstationHighlights") != null){valueMap.put("VillagerWorkstationHighlights", 1);}
					//if(getServer().getPluginManager().getPlugin("RotationalWrench") != null){valueMap.put("RotationalWrench", 1);}
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
		
		consoleInfo("DISABLED");
	}
    
    public void consoleInfo(String state) {
		
		logger.info(ChatColor.GREEN + "**************************************" + ChatColor.RESET);
		logger.info(ChatColor.YELLOW + THIS_NAME + " v" + THIS_VERSION + ChatColor.RESET + " is " + state);
		logger.info(ChatColor.GREEN + "**************************************" + ChatColor.RESET);
	}
	
	public	void log(String dalog){// TODO: log
		
		logger.info(ChatColor.YELLOW + pluginName + " v" + THIS_VERSION + ChatColor.RESET + " " + dalog );
	}
	public	void log(Level level, String dalog){// TODO: log
		
		logger.log(level, ChatColor.YELLOW + pluginName + " v" + THIS_VERSION + ChatColor.RESET + " " + dalog );
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
			String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
			links = links.replace("<DownloadLink>", DownloadLink).replace("<Download>", get("rw.version.download"))
					.replace("<Donate>", get("rw.version.donate")).replace("<please_update>", get("rw.version.please_update"))
					.replace("<Donate_msg>", get("rw.version.donate.message")).replace("<Notes>", get("rw.version.notes"))
					.replace("<Notes_msg>", get("rw.version.notes.message"));
			String versions = "" + ChatColor.GRAY + get("rw.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("rw.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
			player.sendMessage("" + ChatColor.GRAY + get("rw.version.message").toString().replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
			Utils.sendJson(player, links);
			player.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
			// TODO: UpdateCheck onPlayerJoin
			//player.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " v" + UColdVers + ChatColor.RESET + " New version available! " + ChatColor.GREEN + " v" + UCrw.version. + ChatColor.RESET + "\n" + ChatColor.GREEN + UpdateChecker.getResourceUrl() + ChatColor.RESET);
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
		LocalDate localDate = LocalDate.now();
		String daDay = dtf.format(localDate);

		if (daDay.equals("04/16")) {
		    String playerId = player.getUniqueId().toString();
		    if (!triggeredPlayers.contains(playerId)) {
		        if (isPluginRequired(THIS_NAME)) {
		            player.sendTitle("Happy Birthday Mom", "I miss you - 4/16/1954-12/23/2022", 10, 70, 20);
		        }
		        triggeredPlayers.add(playerId);
		    }
		}
		if(player.getName().equals("JoelYahwehOfWar")||player.getName().equals("JoelGodOfWar")){
			player.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
			//p.sendMessage("seed=" + p.getWorld().getSeed());
		}
	}

	public void sendJson(Player player, String string) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + 
		        "\" " + string);
	}
	
	@SuppressWarnings("unlikely-arg-type")
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
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){ // TODO: Commands
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		if (cmd.getName().equalsIgnoreCase("RWrench")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
				sender.sendMessage(ChatColor.RESET + " /RWrench - " + get("rw.command.help") ); // " + get("rw.command.help") 
				sender.sendMessage(ChatColor.RESET + " /RWrench Texture - " + get("rw.command.texture") ); // get("rw.command.texture") 
				if(sender.hasPermission("rotationalwrench.reload")){
					sender.sendMessage(ChatColor.RESET + " /RWrench Reload - " + get("rw.command.reload") ); // get("rw.command.reload")
				}
				if(sender.hasPermission("rotationalwrench.toggledebug")){
					sender.sendMessage(ChatColor.RESET + " /RWrench ToggleDebug/TD - " + get("rw.command.toggledebug") ); // get("rw.command.toggledebug")
				}
				if(sender.hasPermission("rotationalwrench.showUpdateAvailable")){
					sender.sendMessage(ChatColor.RESET + " /RWrench Update - " + get("rw.command.update") ); // get("rw.command.update")
				}
				sender.sendMessage(ChatColor.RESET + " ");
				sender.sendMessage(ChatColor.RESET + " " + get("rw.version.donate") + ": https://ko-fi.com/joelgodofwar");
				sender.sendMessage(ChatColor.RESET + " ");
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
				datafolder = this.getDataFolder().toString();
				colorful_console = getConfig().getBoolean("colorful_console", true);
				loadRecipe();
				sender.sendMessage(ChatColor.YELLOW + pluginName + ChatColor.RESET + get("rw.message.reloaded") ); // get("rw.message.reloaded")
			}
		}
		if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
			if( sender.isOp() || sender.hasPermission("rotationalwrench.toggledebug") || !(sender instanceof Player) ){
				debug = !debug;
				sender.sendMessage(ChatColor.YELLOW + pluginName + ChatColor.RESET + get("rw.message.debugtrue").replace("<boolean>", get("rw.message.boolean." + String.valueOf(debug).toLowerCase())) ); // get("rw.message.debugtrue")
				return true;
			}else if(!sender.hasPermission("rotationalwrench.toggledebug")){
				sender.sendMessage(ChatColor.YELLOW + pluginName + ChatColor.RED + " " + get("rw.message.noperm") ); // get("rw.message.noperm")
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
			if(!(sender instanceof Player)) {
				/** Console has all permissions */
				try {
					Bukkit.getConsoleSender().sendMessage("Checking for updates...");
					VersionChecker updater = new VersionChecker(this, projectID, githubURL);
					if(updater.checkForUpdates()) {
						/** Update available */
						UpdateAvailable = true; // TODO: Update Checker
						UColdVers = updater.oldVersion();
						UCnewVers = updater.newVersion();
						
						log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						log("* " + get("rw.version.message").toString().replace("<MyPlugin>", THIS_NAME) );
						log("* " + get("rw.version.old_vers") + ChatColor.RED + UColdVers );
						log("* " + get("rw.version.new_vers") + ChatColor.GREEN + UCnewVers );
						log("*");
						log("* " + get("rw.version.please_update") );
						log("*");
						log("* " + get("rw.version.download") + ": " + DownloadLink + "/history");
						log("* " + get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
						log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
						//Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
					}else{
						/** Up to date */
						UpdateAvailable = false;
						log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						log("* " + ChatColor.YELLOW + THIS_NAME + ChatColor.RESET + " " + get("rw.version.curvers") + ChatColor.RESET );
						log("* " + get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
						log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					}
				}catch(Exception e) {
					/** Error */
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + get("rw.version.update.error"));
					e.printStackTrace();
				}
				/** end update checker */
				return true;
			}
			String perm = "rotationalwrench.showUpdateAvailable";
			if((sender.isOp()||sender.hasPermission(perm))){
				BukkitTask updateTask = this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					public void run() {
						try {
							Bukkit.getConsoleSender().sendMessage("Checking for updates...");
							VersionChecker updater = new VersionChecker(THIS_VERSION, projectID, githubURL);
							if(updater.checkForUpdates()) {
								UpdateAvailable = true;
								UColdVers = updater.oldVersion();
								UCnewVers = updater.newVersion();
								String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>.\"}}]";
								links = links.replace("<DownloadLink>", DownloadLink).replace("<Download>", get("rw.version.download"))
										.replace("<Donate>", get("rw.version.donate")).replace("<please_update>", get("rw.version.please_update"))
										.replace("<Donate_msg>", get("rw.version.donate.message")).replace("<Notes>", get("rw.version.notes"))
										.replace("<Notes_msg>", get("rw.version.notes.message"));
								String versions = "" + ChatColor.GRAY + get("rw.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("rw.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
								sender.sendMessage("" + ChatColor.GRAY + get("rw.version.message").toString().replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
								Utils.sendJson(sender, links);
								sender.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
							}else{
								String links = "{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}}";
								links = links.replace("<Donate>", get("rw.version.donate")).replace("<Donate_msg>", get("rw.version.donate.message"));
								Utils.sendJson(sender, links);
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " v" + THIS_VERSION + ChatColor.RESET + " " + get("rw.version.curvers") + ChatColor.RESET);
								UpdateAvailable = false;
							}
						}catch(Exception e) {
							sender.sendMessage(ChatColor.RED + get("rw.version.update.error"));
							e.printStackTrace();
						}
					}
				});
				return true;
			}else{
				sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + get("rw.message.noperm").replace("<perm>", perm));
				return false;
			}
		}
		return false;
	}
	
	@Override 
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		if (command.getName().equalsIgnoreCase("RWrench")) {
			List<String> autoCompletes = new ArrayList<>(); //create a new string list for tab completion
			if (args.length == 1) { // reload, toggledebug, playerheads, customtrader, headfix
				autoCompletes.add("texture");
				autoCompletes.add("reload");
				autoCompletes.add("toggledebug");
				autoCompletes.add("update");
				return autoCompletes; // then return the list
			}
		}
		return null;
	}
	
	public boolean isDebug(){
		return debug;
	}
	
	public boolean getBoolean(String string, boolean bool){
		return getConfig().getBoolean(string, bool);
	}
	@SuppressWarnings("unchecked")
	public void loadRecipe(){
		try{ // TODO: Add Recipe
	    	ItemMeta meta = Objects.requireNonNull(wrench.getItemMeta());
	        meta.setDisplayName(ChatColor.RESET + getConfig().getString("wrench_recipe.translation", "Rotational Wrench"));
	        meta.setUnbreakable(getConfig().getBoolean("wrench_recipe.unbreakable", true));
	        meta.setCustomModelData(4321);
	        wrench.setItemMeta(meta);
	        //String row1 = "";String row2 = "";String row3 = "";
	        recipe = new ShapedRecipe(RECIPE_KEY, wrench);
			List<String> shape = (List<String>) getConfig().get("wrench_recipe.shape", new String[]{" g ", " gg", "i  "});
			String line1 = shape.get(0);
			String line2 = shape.get(1);
			String line3 = shape.get(2);
			recipe.shape(line1, line2, line3);
			List<String> ingredients = (List<String>) getConfig().get("wrench_recipe.ingredients", new String[]{"g:GOLD_INGOT", "i:IRON_INGOT"});
			for(String I : ingredients){
				String[] breakdown = I.split(":");
				char lin1 = breakdown[0].charAt(0);
				String lin2 = breakdown[1];
				Material mi = Material.matchMaterial(lin2);
				recipe.setIngredient(lin1, mi);
			}
	        		//.shape(" g "," gg","i  ")
	                //.setIngredient('g', Material.GOLD_INGOT)
	                //.setIngredient('i', Material.IRON_INGOT);
	        Bukkit.addRecipe(recipe);
	        log("Rotational Wrench Recipe added.");
		}catch(Exception e){}
	}
	
	public String LoadTime(long startTime) {
	    long elapsedTime = System.currentTimeMillis() - startTime;
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
	    long milliseconds = elapsedTime % 1000;
	    
	    if (minutes > 0) {
	        return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
	    } else if (seconds > 0) {
	        return String.format("%d s %d ms.", seconds, milliseconds);
	    } else {
	        return String.format("%d ms.", elapsedTime);
	    }
	}
	
	@SuppressWarnings("static-access")
	public String get(String key, String... defaultValue) {
		return lang2.get(key, defaultValue);
	}
	
	public boolean isPluginRequired(String pluginName) {
	    String[] requiredPlugins = {"SinglePlayerSleep", "MoreMobHeads", "NoEndermanGrief", "ShulkerRespawner", "DragonDropElytra", "RotationalWrench", "SilenceMobs", "VillagerWorkstationHighlights"};
	    for (String requiredPlugin : requiredPlugins) {
	        if (getServer().getPluginManager().getPlugin(requiredPlugin) != null && getServer().getPluginManager().isPluginEnabled(requiredPlugin)) {
	            if (requiredPlugin.equals(pluginName)) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
}
