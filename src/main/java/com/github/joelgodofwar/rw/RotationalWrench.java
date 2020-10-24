package com.github.joelgodofwar.rw;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.block.data.type.Chain;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.joelgodofwar.rw.util.Ansi;
import com.github.joelgodofwar.rw.util.FileStuff;
import com.github.joelgodofwar.rw.util.RotateHelper;
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
	YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static PluginDescriptionFile pdfFile;
	static String datafolder;
	boolean colorful_console = true;
	boolean UpdateAvailable =	false;
	
	private final String resourcePackUrl = "https://github.com/JoelGodOfwar/RotationalWrench/raw/main/resource/RWrench.zip";
    private final byte[] hash = new BigInteger("1ACF79C491B3CB9EEE50816AD0CC1FC45AABA147", 16).toByteArray();
    private final NamespacedKey RECIPE_KEY = new NamespacedKey(this, "rotational_wrench");
    private final List<BlockFace> faces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);
    /**private final List<BlockFace> skullfaces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST
    		, BlockFace.NORTH_EAST, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST
    		, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST);*/
    final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
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
			if(!checkconfigversion.equalsIgnoreCase("1.0.3")){
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
	public boolean isDoubleChest(Block block){
		BlockState state = block.getState();
		org.bukkit.block.data.type.Chest chest = (org.bukkit.block.data.type.Chest) state.getBlockData();
		org.bukkit.block.data.type.Chest.Type type = chest.getType();
		switch(type){
		case SINGLE:
			return false;
		case LEFT:
			return true;
		case RIGHT:
			return true;
		}
		return false;
		
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	@EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
		
        Block block = event.getClickedBlock();
        /**Chest chest = (Chest) block.getBlockData();
        chest.getType();
        DoubleChest dc = (DoubleChest) block.getBlockData();*/
        /** Redstone, Terracotta, Stairs */
        if(  !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		( Tags.REDSTONE_COMPONENTS.isTagged(block.getType()) && config.getBoolean("enabled.redstone", true) || 
        				Tags.GLAZED_TERRACOTTA.isTagged(block.getType()) && config.getBoolean("enabled.terracotta", true) || 
        				Tags.STAIRS.isTagged(block.getType()) && config.getBoolean("enabled.stairs.rotate", true)  || 
        				 
        				Tags.FENCE_GATES.isTagged(block.getType()) && config.getBoolean("enabled.fencegates", true) ||
        				Tags.DOORS.isTagged(block.getType()) && config.getBoolean("enabled.doors", true) ||
        				Tags.WORKSTATIONS.isTagged(block.getType()) && config.getBoolean("enabled.workstations", true) ||
        				Tags.CARVED_PUMPKIN.isTagged(block.getType()) && config.getBoolean("enabled.carvedpumpkin", true) ||
        				Tags.END_ROD.isTagged(block.getType()) && config.getBoolean("enabled.endrod", true) ) ) {
        	//if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) ){
        		Directional state = (Directional) block.getBlockData(); // Directional state = (Directional) block.getBlockData();
                int facing = faces.indexOf(state.getFacing());
                BlockFace nextFace = null;
                int i = 0;
                while (nextFace == null || !state.getFaces().contains(nextFace)) {
                    if (i >= 6) throw new IllegalStateException("Infinite loop detected");
                    nextFace = event.getPlayer().isSneaking() ? facing - 1 < 0 ? faces.get(facing + 6 - 1) : faces.get(facing - 1) : faces.get((facing + 1) % 6); // 
                    facing = faces.indexOf(nextFace);
                    i++;
                }
                event.setUseInteractedBlock(Result.DENY);
                state.setFacing(nextFace);
                block.setBlockData(state);
                //log("facing=" + facing);
                //log("nextFace=" + nextFace.toString());
            //}
        }
        
        /** Chests */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.CHESTS.isTagged(block.getType()) && config.getBoolean("enabled.chests", true) ){
        	//org.bukkit.block.data.type.Chest chest = (org.bukkit.block.data.type.Chest) block.getBlockData();
        	BlockState state = block.getState();
    		Location location = block.getLocation();
    		int X = location.getBlockX();
    		int Y = location.getBlockY();
    		int Z = location.getBlockZ();
    		Location location2 = null;
    		World world = location.getWorld();
    		Block sister = null;
    		event.setUseInteractedBlock(Result.DENY);
    		Block block2 = null;
    		BlockState state2 = null;
        	//if(isDoubleChest(block)){
        		/**Chest leftchest;
        		Chest rightchest;
        	    InventoryHolder holder = ((DoubleChest) chest).getInventory().getHolder();
        	    if (holder instanceof DoubleChest) {
        	        DoubleChest doublechest = ((DoubleChest) holder);
        	        leftchest = (Chest) doublechest.getLeftSide();
        	        rightchest = (Chest) doublechest.getRightSide();
        	        
        	    }*/
    			if( !(((org.bukkit.block.Chest)block.getState()).getInventory() instanceof DoubleChestInventory) ) {
	    			Directional dir = (Directional)block.getBlockData();
	    			if(dir.getFacing() == BlockFace.EAST) dir.setFacing(BlockFace.NORTH);
	    			else if(dir.getFacing() == BlockFace.NORTH) dir.setFacing(BlockFace.WEST);
	    			else if(dir.getFacing() == BlockFace.WEST) dir.setFacing(BlockFace.SOUTH);
	    			else if(dir.getFacing() == BlockFace.SOUTH) dir.setFacing(BlockFace.EAST);
	    			block.setBlockData(dir);
        		}
    			if(event.getPlayer().isSneaking()){
	    			BlockData data = state.getBlockData();
	    			Chest chest = (Chest) data;
	        		BlockFace main_dir = chest.getFacing(), left_dir = RotateHelper.getLeftDirection(main_dir), right_dir = RotateHelper.getRightDirection(main_dir);
	    			org.bukkit.block.data.type.Chest.Type type = chest.getType(), new_type = null;
	    			Block second_part = null, new_part = null;
	        	    
	        	    BlockFace facing = chest.getFacing();
	        	    switch(type){
	        	    case LEFT:
	        	    	second_part = block.getRelative(right_dir);
	    				new_part = block.getRelative(left_dir);
	    				type = org.bukkit.block.data.type.Chest.Type.RIGHT;
	    				new_type = org.bukkit.block.data.type.Chest.Type.LEFT;
	        	    	break;
	        	    case RIGHT:
	        	    	second_part = block.getRelative(left_dir);
	    				new_part = null;
	    				type = org.bukkit.block.data.type.Chest.Type.SINGLE;
	    				new_type = null;
	        	    	break;
	        	    case SINGLE:
	        	    	second_part = null;
	    				new_part = block.getRelative(right_dir);
	    				type = org.bukkit.block.data.type.Chest.Type.LEFT;
	    				new_type = org.bukkit.block.data.type.Chest.Type.RIGHT;
	    				if(new_part.getType() != block.getType() || ((Chest) new_part.getBlockData()).getFacing() != chest.getFacing()) {
	    					new_part = block.getRelative(left_dir);
	    					type = org.bukkit.block.data.type.Chest.Type.RIGHT;
	    					new_type = org.bukkit.block.data.type.Chest.Type.LEFT;
	    				}
	        	    	break;
	        	    }
	        	    if(second_part != null) {
	    				Chest old_part = ((Chest) second_part.getBlockData());
	    				old_part.setType(org.bukkit.block.data.type.Chest.Type.SINGLE);
	    				second_part.setBlockData(old_part);
	    			}
	    			if(new_part != null && new_part.getType() == block.getType()) {
	    				Chest new_chest = (Chest) new_part.getBlockData();
	    				if(new_chest.getFacing() == chest.getFacing() && new_chest.getType() == org.bukkit.block.data.type.Chest.Type.SINGLE) {
	    					new_chest.setType(new_type);
	    					new_part.setBlockData(new_chest);
	    				}
	    				else type = org.bukkit.block.data.type.Chest.Type.SINGLE;
	    			}
	    			else type = org.bukkit.block.data.type.Chest.Type.SINGLE;
	    			
	    			if(type == chest.getType())
	    				return;
	    			
	    			chest.setType(type);
	    			block.setBlockData(chest);
    			}
        	    /**BlockState state2 = sister.getState();
        	    BlockData data = state2.getBlockData();
                Chest bed = (Chest) data;
        	    bed.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
        	    state2.setBlockData(bed);
        	    state2.update(true, true);*/
        	//}
        	
        }
        
        /** Heads */ //skullfaces
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		block.getType() == Material.PLAYER_HEAD && config.getBoolean("enabled.logs", true) ){
        	BlockState state = block.getState();
        	Skull skull = (Skull) block.getState();
        	BlockFace blockFace = null;
        	BlockFace rotation = skull.getRotation();
        	 if( !event.getPlayer().isSneaking() ){
        		 
        	 }
        	switch(rotation){
        	case NORTH: 			// 1 
           	 	if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.NORTH_NORTH_EAST;
           	 	}else if( event.getPlayer().isSneaking() ){
           	 	blockFace =  BlockFace.NORTH_NORTH_WEST;
           	 	}
        		break;
        	case NORTH_NORTH_EAST: 	// 2 
           	 	if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.NORTH_EAST;
           	 	}else if( event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.NORTH;
           	 	}
        		break;
        	case NORTH_EAST: 		// 3 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.EAST_NORTH_EAST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.NORTH_NORTH_EAST;
           	 	}
        		break;
        	case EAST_NORTH_EAST: 	// 4 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.EAST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.NORTH_EAST;
           	 	}
        		break;
        	case EAST: 				// 5 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.EAST_SOUTH_EAST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.EAST_NORTH_EAST;
           	 	}
        		break;
        	case EAST_SOUTH_EAST: 	// 6 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.SOUTH_EAST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.EAST;
           	 	}
        		break;
        	case SOUTH_EAST: 		// 7 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.SOUTH_SOUTH_EAST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.EAST_SOUTH_EAST;
           	 	}
        		break;
        	case SOUTH_SOUTH_EAST: 	// 8 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.SOUTH;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.SOUTH_EAST;
           	 	}
        		break;
        	case SOUTH: 			// 9 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.SOUTH_SOUTH_WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.SOUTH_SOUTH_EAST;
           	 	}
        		break;
        	case SOUTH_SOUTH_WEST: 	// 10 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.SOUTH_WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.SOUTH;
           	 	}
        		break;
        	case SOUTH_WEST: 		// 11 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.WEST_SOUTH_WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.SOUTH_SOUTH_WEST;
           	 	}
        		break;
        	case WEST_SOUTH_WEST: 	// 12 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.SOUTH_WEST;
           	 	}
        		break;
        	case WEST: 				// 13 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.WEST_NORTH_WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.WEST_SOUTH_WEST;
           	 	}
        		break;
        	case WEST_NORTH_WEST: 	// 14 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.NORTH_WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.WEST;
           	 	}
        		break;
        	case NORTH_WEST: 		// 15 
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.NORTH_NORTH_WEST;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.WEST_NORTH_WEST;
           	 	}
        		break;
        	case NORTH_NORTH_WEST: 	// 16 blockFace =  BlockFace.NORTH_NORTH_WEST;
        		if( !event.getPlayer().isSneaking() ){
           	 		blockFace =  BlockFace.NORTH;
        		}else if( event.getPlayer().isSneaking() ){
        			blockFace =  BlockFace.NORTH_WEST;
           	 	}
        		break;
			default:
				break;
        	}
        	skull.setRotation(blockFace);
        	skull.update(true, true);
        	//block.setBlockData(skull);
            event.setUseInteractedBlock(Result.DENY);
        }
        
        /** Logs */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.LOGS.isTagged(block.getType()) && config.getBoolean("enabled.logs", true) ){
        	Orientable state = (Orientable) block.getBlockData(); // Directional state = (Directional) block.getBlockData();
            Axis axis = state.getAxis();
            Orientable log = ((Orientable) Material.getMaterial(block.getType().toString()).createBlockData());
            switch(axis){
        	case X:
        		log.setAxis(Axis.Y);
        		break;
        	case Y:
        		log.setAxis(Axis.Z);
        		break;
        	case Z:
        		log.setAxis(Axis.X);
        		break;
        	}
            block.setBlockData(log);
            event.setUseInteractedBlock(Result.DENY);
        }
        
        /** Stairs */
        if( event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.STAIRS.isTagged(block.getType()) && config.getBoolean("enabled.stairs.invert", true) ){
        	BlockState state = block.getState();
        	Stairs stairs = (Stairs) state.getBlockData();
        	
			Half half = stairs.getHalf();
        	switch(half){
        	case BOTTOM:
        		stairs.setHalf(Half.TOP);
        		break;
        	case TOP:
        		stairs.setHalf(Half.BOTTOM);
        		break;
			default:
				break;
        	}
        	state.setBlockData(stairs);
            state.update(false, false);
        }
        /** Slabs */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.SLABS.isTagged(block.getType()) && config.getBoolean("enabled.slabs", true) ){
        	BlockState state = block.getState();
        	Slab slab = (Slab) state.getBlockData();
			Type type = slab.getType();
			log("type=" + type);
			switch(type){
			case BOTTOM:
				slab.setType(Type.TOP);
				state.setBlockData(slab);
	            state.update(false, false);
				break;
			case TOP:
				slab.setType(Type.BOTTOM);
				state.setBlockData(slab);
	            state.update(false, false);
				break;
			case DOUBLE:
				break;
			}
        }
        /** Beds */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.BEDS.isTagged(block.getType()) && config.getBoolean("enabled.beds", true) ){
        	BlockState state = block.getState();
        	Bed bed = (Bed) state.getBlockData();
			BlockFace face = bed.getFacing();
			Material material = block.getType();
			Location loc = block.getLocation();
			Location oldloc = null;
			// Get Bed Parts
			Part part = bed.getPart();
			Part part1 = null;
			Part part2 = null;
			
	        World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	
	        int X2 = X;
	        int Z2 = Z;
	        BlockFace blockFace = null;
	        /** Clockwise */
	        if(!event.getPlayer().isSneaking() && part == Part.FOOT){
	        	//log(Ansi.RED + "Part is FOOT." + Ansi.RESET);
	        	if(face == BlockFace.NORTH){
		        	if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.EAST){
		        	if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.SOUTH){
		        	if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.WEST){
		        	if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST; /** Location of old Bed.Head */
			            oldloc = loc.subtract(1, 0, 0);
			        }else {
			            return;
			        }
	        	}
	        /** Counter Clockwise */
	        }else if(event.getPlayer().isSneaking() && part == Part.FOOT){	        	
	        	if(face == BlockFace.NORTH){
	        		if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.EAST){
	        		if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.SOUTH){
	        		if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.WEST){
	        		if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2=X+1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2 = X-1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}
	        /***********************************************************************************/	
	        }else if(!event.getPlayer().isSneaking() && part == Part.HEAD){
	        	//log(Ansi.RED + "Part is HEAD." + Ansi.RESET);
	        	if(face == BlockFace.NORTH){
	        		if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.EAST){
	        		if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.SOUTH){
	        		if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.WEST){
	        		if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}
	        /** Counter Clockwise */
	        }else if(event.getPlayer().isSneaking() && part == Part.HEAD){
	        	if(face == BlockFace.NORTH){
	        		if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.EAST){
	        		if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.SOUTH){
	        		if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.subtract(0, 0, 1); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}else if(face == BlockFace.WEST){
	        		if (world.getBlockAt(X,Y,Z-1).isEmpty()){
			            X2=X;
			            Z2=Z-1;
			            blockFace=BlockFace.SOUTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X-1,Y,Z).isEmpty()){
			            X2=X-1;
			            Z2 = Z;
			            blockFace = BlockFace.EAST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X,Y,Z+1).isEmpty()){
			            X2=X;
			            Z2=Z+1;
			            blockFace=BlockFace.NORTH;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else if (world.getBlockAt(X+1,Y,Z).isEmpty()){
			            X2 = X+1;
			            Z2 = Z;
			            blockFace = BlockFace.WEST;
			            oldloc = loc.add(1, 0, 0); /** Location of old Bed.Head */
			        }else {
			            return;
			        }
	        	}
	        }
	        //event.getPlayer().sendMessage("x= " + X + " y= " + Y + " z= " + Z + " x2= " + X2 + " z2= " + Z2);
	        switch(part){
			case HEAD:
				//event.getPlayer().sendMessage("That is the bed's head");
				setBed(block, blockFace, material);
				//MakeBedPart(Bed.Part.HEAD, blockFace, new Location(world,X,Y,Z), material);
				//MakeBedPart(Bed.Part.FOOT, blockFace, new Location(world,X2,Y,Z2), material);
				break;
			case FOOT:
				//event.getPlayer().sendMessage("That is the bed's foot");
				MakeBedPart(Bed.Part.HEAD, blockFace, new Location(world,X2,Y,Z2), material);
				MakeBedPart(Bed.Part.FOOT, blockFace, new Location(world,X,Y,Z), material);
				break;
			}
	        oldloc.getBlock().setType(Material.AIR);
	        event.setUseInteractedBlock(Result.DENY);
        }
        
        /** Chain */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		block.getType() == Material.CHAIN && config.getBoolean("enabled.chain", true) ){
        	BlockState state = block.getState();
        	Chain chain = (Chain) state.getBlockData();
        	Axis axis = chain.getAxis();
        	switch(axis){
        	case X:
        		chain.setAxis(Axis.Y);
        		break;
        	case Y:
        		chain.setAxis(Axis.Z);
        		break;
        	case Z:
        		chain.setAxis(Axis.X);
        		break;
        	}
        	state.setBlockData(chain);
            state.update(false, false);
        }
        
        /** Trapdoors */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.TRAPDOORS.isTagged(block.getType())){
        	BlockState state = block.getState();
    		TrapDoor trapdoor = (TrapDoor) state.getBlockData();
    		BlockFace facing = trapdoor.getFacing();
    		Half half = trapdoor.getHalf();
    		event.setUseInteractedBlock(Result.DENY);
    		if(event.getPlayer().isSneaking() && config.getBoolean("enabled.trapdoors.invert", true) ){
	    		/** half */
	    		switch(half){
	    		case TOP:
	    			trapdoor.setHalf(Half.BOTTOM);
					state.setBlockData(trapdoor);
		            state.update(false, false);
	    			break;
	    		case BOTTOM:
	    			trapdoor.setHalf(Half.TOP);
					state.setBlockData(trapdoor);
		            state.update(false, false);
	    			break;
	    		}
    		}else if(!event.getPlayer().isSneaking() && config.getBoolean("enabled.trapdoors.rotate", true) ){
    			//log("true");
	    		/** facing */
	    		switch(facing){
	    		case NORTH:
	    			trapdoor.setFacing(BlockFace.EAST);
					state.setBlockData(trapdoor);
		            state.update(false, false);
	    			break;
	    		case EAST:
	    			trapdoor.setFacing(BlockFace.SOUTH);
					state.setBlockData(trapdoor);
		            state.update(false, false);
	    			break;
	    		case SOUTH:
	    			trapdoor.setFacing(BlockFace.WEST);
					state.setBlockData(trapdoor);
		            state.update(false, false);
	    			break;
	    		case WEST:
	    			trapdoor.setFacing(BlockFace.NORTH);
					state.setBlockData(trapdoor);
		            state.update(false, false);
	    			break;
				default:
					//log("default");
					break;
	    		}
	    		//log("facing=" + facing);
    		}
        }
    	
        /** Rails */
        if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		Tags.RAILS.isTagged(block.getType()) && config.getBoolean("enabled.rails", true) ){
        	BlockState state = block.getState();
        	Rail rail = (Rail) block.getBlockData();
			Shape shape = rail.getShape();
			log("shape=" + shape.toString());
			switch(shape){
			case ASCENDING_EAST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.ASCENDING_NORTH);
				}else{
					rail.setShape(Shape.ASCENDING_SOUTH);
				}
				break;
			case ASCENDING_SOUTH:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.ASCENDING_EAST);
				}else{
					rail.setShape(Shape.ASCENDING_WEST);
				}
				break;
			case ASCENDING_WEST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.ASCENDING_SOUTH);
				}else{
					rail.setShape(Shape.ASCENDING_NORTH);
				}
				break;
			case ASCENDING_NORTH:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.ASCENDING_WEST);
				}else{
					rail.setShape(Shape.ASCENDING_EAST);
				}
				break;
				
			case EAST_WEST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.SOUTH_WEST);
				}else{
					rail.setShape(Shape.NORTH_EAST);
				}
				break;
			case NORTH_EAST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.EAST_WEST);
				}else{
					rail.setShape(Shape.NORTH_SOUTH);
				}
				break;
			case NORTH_SOUTH:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.NORTH_EAST);
				}else{
					rail.setShape(Shape.NORTH_WEST);
				}
				break;
			case NORTH_WEST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.NORTH_SOUTH);
				}else{
					rail.setShape(Shape.SOUTH_EAST);
				}
				break;
			case SOUTH_EAST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.NORTH_WEST);
				}else{
					rail.setShape(Shape.SOUTH_WEST);
				}
				break;
			case SOUTH_WEST:
				if( event.getPlayer().isSneaking() ){
					rail.setShape(Shape.SOUTH_EAST);
				}else{
					rail.setShape(Shape.EAST_WEST);
				}
				break;
			}
			state.setBlockData(rail);
            state.update(false, false);

			//rail.setShape(paramShape);
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
	
	public void MakeBedPart(Bed.Part part, BlockFace blockFace, Location location, Material material){
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        BlockState state = block.getState();
        state.setType(material);
        state.update(true);
        BlockData data = state.getBlockData();
        Bed bed = (Bed) data;
        bed.setPart(part);
        bed.setFacing(blockFace);
        state.setBlockData(data);
        state.update(true);
    }
	
	public void setBed(Block start, BlockFace facing, Material material) {
			start.setBlockData(Bukkit.createBlockData(material, (data) -> {
	           ((Bed) data).setPart(Bed.Part.HEAD);
	           ((Bed) data).setFacing(facing);
	           
	        }));
	        start = start.getRelative(facing.getOppositeFace());
	        start.setBlockData(Bukkit.createBlockData(material, (data) -> {
	           ((Bed) data).setPart(Bed.Part.FOOT);
	           ((Bed) data).setFacing(facing);
	           
	        }));
	        start = start.getRelative(facing.getOppositeFace());
	        
	    
	    /**for (Bed.Part part : Bed.Part.values()) {
	        start.setBlockData(Bukkit.createBlockData(material, (data) -> {
	           ((Bed) data).setPart(part);
	           ((Bed) data).setFacing(facing);
	           log("part=" + part);
	        }));
	        start = start.getRelative(facing.getOppositeFace());
	    }*/
	}
	
	@EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent event) {
		Entity entity = event.getRightClicked();
        /** Armor Stands */
        if(   !event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().equals(wrench)  
        		&& entity instanceof ArmorStand && config.getBoolean("enabled.armorstands", true) ){
        	ArmorStand as = (ArmorStand) entity;
        	float yaw = as.getLocation().getYaw();
        	yaw = yaw + 45;
        	Location loc = as.getLocation();
        	loc.setYaw(yaw);
			as.teleport(loc);
			log("yaw=" + yaw);

        }if( event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().equals(wrench)  
        		&& entity instanceof ArmorStand && config.getBoolean("enabled.armorstands", true) ){
        	ArmorStand as = (ArmorStand) entity;
        	float yaw = as.getLocation().getYaw();
        	yaw = yaw + 1;
        	Location loc = as.getLocation();
        	loc.setYaw(yaw);
			as.teleport(loc);
			log("yaw=" + yaw);
        }
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
