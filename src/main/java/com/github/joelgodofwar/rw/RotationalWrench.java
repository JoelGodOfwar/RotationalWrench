package com.github.joelgodofwar.rw;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
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

import com.github.joelgodofwar.rw.nms.RW_1_14_R1;
import com.github.joelgodofwar.rw.nms.RW_1_16_R2;
import com.github.joelgodofwar.rw.util.Ansi;
import com.github.joelgodofwar.rw.util.FileStuff;
import com.github.joelgodofwar.rw.util.Tags;
import com.github.joelgodofwar.rw.util.YmlConfiguration;
import com.google.common.collect.Lists;

public class RotationalWrench extends JavaPlugin implements Listener{
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static boolean UpdateCheck;
	public String UColdVers;
	public String UCnewVers;
	public static boolean debug = false;
	public static String daLang;
	public YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static PluginDescriptionFile pdfFile;
	static String datafolder;
	boolean colorful_console = true;
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
    	PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.AnsiColor("GREEN", colorful_console) + "**************************************" + Ansi.AnsiColor("RESET", colorful_console));
		logger.info(Ansi.AnsiColor("YELLOW", colorful_console) + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.AnsiColor("RESET", colorful_console) + " Loading...");
		
    	/**	Check for config */
		try{
			if(!getDataFolder().exists()){
				log("Data Folder doesn't exist");
				log("Creating Data Folder");
				getDataFolder().mkdirs();
				log("Data Folder Created at " + getDataFolder());
			}
			File	file = new File(getDataFolder(), "config.yml");
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
			if(!checkconfigversion.equalsIgnoreCase("1.0.4")){
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
			
			config.set("enabled.redstone", oldconfig.get("enabled.redstone", true));
			config.set("enabled.terracotta", oldconfig.get("enabled.terracotta", true));
			config.set("enabled.stairs.rotate", oldconfig.get("enabled.stairs.rotate", true));
			config.set("enabled.stairs.invert", oldconfig.get("enabled.stairs.invert", true));
			config.set("enabled.slabs", oldconfig.get("enabled.slabs", true));
			config.set("enabled.armorstands", oldconfig.get("enabled.armorstands", true));
			config.set("enabled.rails", oldconfig.get("enabled.rails", true));
			config.set("enabled.beds", oldconfig.get("enabled.beds", true));
			config.set("enabled.chain", oldconfig.get("enabled.chain", true));
			config.set("enabled.trapdoors.rotate", oldconfig.get("enabled.trapdoors.rotate", true));
			config.set("enabled.trapdoors.invert", oldconfig.get("enabled.trapdoors.invert", true));
			config.set("enabled.carvedpumpkin", oldconfig.get("enabled.carvedpumpkin", true));
			config.set("enabled.chests", oldconfig.get("enabled.chests", true));
			config.set("enabled.fencegates", oldconfig.get("enabled.fencegates", true));
			config.set("enabled.doors", oldconfig.get("enabled.doors", true));
			config.set("enabled.workstations", oldconfig.get("enabled.workstations", true));
			config.set("enabled.endrod", oldconfig.get("enabled.endrod", true));
			config.set("enabled.logs", oldconfig.get("enabled.logs", true));
			config.set("enabled.heads", oldconfig.get("enabled.heads", true));
			config.set("enabled.bell", oldconfig.get("enabled.bell", true));

			try {
				config.save(new File(getDataFolder(), "config.yml"));
			} catch (IOException e) {
				logWarn("Could not save old settings to config.yml");
				e.printStackTrace();
			}
			log("config.yml has been updated");
		}else{
			//log("" + "not found");
		}
		/** End Config update check */
		
    	UpdateCheck = config.getBoolean("auto_update_check", true);
		debug = config.getBoolean("debug", false);
		daLang = config.getString("lang", "en_US");
		oldconfig = new YamlConfiguration();
		pdfFile = this.getDescription();
		datafolder = this.getDataFolder().toString();
		colorful_console = config.getBoolean("colorful_console", true);
		
    	/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			log("jarfile contains dev, debug set to true.");
		}
		
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
        
        String packageName = this.getServer().getClass().getPackage().getName();
    	String version = packageName.substring(packageName.lastIndexOf('.') + 2);
    	log("version=" + version);
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
    }
    
    @Override // TODO: onDisable
	public void onDisable(){
		/** Experimental Code */
		Bukkit.removeRecipe(RECIPE_KEY);
		/** Experimental Code */
		
		consoleInfo("disabled");
	}
    
    public void consoleInfo(String state) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.AnsiColor("GREEN", colorful_console) + "**************************************" + Ansi.AnsiColor("RESET", colorful_console));
		logger.info(Ansi.AnsiColor("YELLOW", colorful_console) + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.AnsiColor("RESET", colorful_console) + " is " + state);
		logger.info(Ansi.AnsiColor("GREEN", colorful_console) + "**************************************" + Ansi.AnsiColor("RESET", colorful_console));
	}
	
	public	void log(String dalog){// TODO: log
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.AnsiColor("YELLOW", colorful_console) + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.AnsiColor("RESET", colorful_console) + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(Ansi.AnsiColor("RED", colorful_console) + "[DEBUG] " + Ansi.AnsiColor("RESET", colorful_console) + dalog);
	}
	public void logWarn(String dalog){
		log(Ansi.AnsiColor("RED", colorful_console) + "[WARN] " + Ansi.AnsiColor("RESET", colorful_console)  + dalog);
	}
    
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event){
	    Player player = event.getPlayer();
	    
	    player.discoverRecipe(RECIPE_KEY);
	    if(player.isOp() && UpdateCheck){	
			
		}
	    /** DEV check **/
		//File jarfile = this.getFile().getAbsoluteFile();
	    if(player.getDisplayName().equals("JoelYahwehOfWar")||player.getDisplayName().equals("JoelGodOfWar")){//&&!(jarfile.toString().contains("-DEV"))){
	    	player.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
	    }
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event){
		if(event.getRecipe().equals(wrench)){
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
				sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("reload")){
			if( sender.isOp() || sender.hasPermission("rotationalwrench.reload") || !(sender instanceof Player) ){
				getServer().getPluginManager().disablePlugin(this);
				getServer().getPluginManager().enablePlugin(this);
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
		return false;
	}
}
