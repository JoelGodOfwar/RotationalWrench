package com.github.joelgodofwar.rw.nms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.block.data.type.Bell;
import org.bukkit.block.data.type.Bell.Attachment;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.Comparator.Mode;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.Switch.Face;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.event.one14.Coral_Fan;
import com.github.joelgodofwar.rw.util.RotateHelper;
import com.github.joelgodofwar.rw.util.RotateHelper114;
import com.github.joelgodofwar.rw.util.StrUtils;
import com.github.joelgodofwar.rw.util.Tags;
import com.github.joelgodofwar.rw.util.Tags_116;
import com.github.joelgodofwar.rw.util.YmlConfiguration;
import com.google.common.collect.Lists;


@SuppressWarnings("deprecation")
public class RW_1_14_R1 implements Listener{
	/**
    1.8		1_8_R1		1.8.3	1_8_R2
	1.8.8 	1_8_R3
	1.9		1_9_R1		1.9.4	1_9_R2	
	1.10	1_10_R1
	1.11	1_11_R1
	1.12	1_12_R1
	1.13	1_13_R1		1.13.1	1_13_R2
	1.14	1_14_R1
	1.15	1_15_R1
	1.16.1	1_16_R1		1.16.2	1_16_R2
	*/
	private RotationalWrench plugin;
	private final List<BlockFace> faces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);
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
	//boolean colorful_console = true;
	boolean UpdateAvailable =	false;
	public final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
	boolean v1_15_R = false;
	public HashMap<UUID, Long> spamfilter =  new HashMap<UUID, Long>();
	public final RotationalWrench RW;
	public RotateHelper114 RH;
	Coral_Fan coral_fan;// = new Coral_Fan();
	
	public RW_1_14_R1(RotationalWrench plugin){
		this.config = plugin.config;
		//wrench = plugin.wrench;
		RW = plugin;
		RH = new RotateHelper114(RW);
		coral_fan = new Coral_Fan(RW);
		this.plugin = plugin;
		ItemMeta meta = Objects.requireNonNull(wrench.getItemMeta());
        meta.setDisplayName(ChatColor.RESET + "Rotational Wrench");
        meta.setUnbreakable(true);
        meta.setCustomModelData(4321);
        wrench.setItemMeta(meta);
        String packageName = plugin.getServer().getClass().getPackage().getName();
    	String version = packageName.substring(packageName.lastIndexOf('.') + 2);
    	if( version.contains("1_15_R") ){
    		v1_15_R = true;
    	}
    	try {
			config.load(new File(plugin.getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			logWarn("Could not load config.yml");
			e1.printStackTrace();
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
	
	@SuppressWarnings({ "unused", "incomplete-switch", "static-access" })
	@EventHandler
    public void onBlockClick(PlayerInteractEvent event) { // TODO: Top
		Block block = event.getClickedBlock();
        boolean onblacklist = false;
        if(!event.getPlayer().hasPermission("rotationalwrench.use")){
        	return;
        }
        onblacklist = StrUtils.stringContains(config.getString("blacklist", ""),event.getPlayer().getWorld().getName());
        if(debug)if(event.getAction() == Action.RIGHT_CLICK_BLOCK)logDebug("Material=" + block.getType().toString());
        /** Spam filter */
        boolean isSpam = false;
        Player player = event.getPlayer();
        long timer = 0;
		long time = (System.currentTimeMillis() - 1497580000000L);
		//log("time=" + time);
        if(spamfilter.get(player.getUniqueId()) == null){
			spamfilter.put(player.getUniqueId(), time);
		}else{
			if(debug){logDebug("spamfilter UUID !null");}
			long spamtime = config.getLong("spamfilter", 250);
			// Player is on the list.
			timer = spamfilter.get(player.getUniqueId());
			if(debug){logDebug("time=" + time);}
			if(debug){logDebug("timer=" + timer);}
			if(debug){logDebug("time - timer=" +  (time - timer));}
			if(debug){logDebug("spamfilter=" + spamtime);}
			// if !time - timer > limit
			if(!((time - timer) > spamtime)){
				isSpam = true;
				event.setCancelled(true);
				return;
			}else if((time - timer) > spamtime){
				if(debug){logDebug("time - timer > spamfilter");}
				spamfilter.replace(player.getUniqueId(), time);
				isSpam = false;
			}
		}
        
        /** Redstone, Terracotta, Stairs */
        if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&
        		( Tags.REDSTONE_COMPONENTS.isTagged(block.getType()) && config.getBoolean("enabled.redstone", true) || 
        				Tags.GLAZED_TERRACOTTA.isTagged(block.getType()) && config.getBoolean("enabled.terracotta", true) || 
        				//Tags.STAIRS.isTagged(block.getType()) && config.getBoolean("enabled.stairs.rotate", true)  || 
        				Tags.FENCE_GATES.isTagged(block.getType()) && config.getBoolean("enabled.fencegates", true) ||
        				Tags.DOORS.isTagged(block.getType()) && config.getBoolean("enabled.doors", true) ||
        				Tags.WORKSTATIONS.isTagged(block.getType()) && config.getBoolean("enabled.workstations", true) ||
        				Tags.CARVED_PUMPKIN.isTagged(block.getType()) && config.getBoolean("enabled.carvedpumpkin", true) ||
        				Tags.END_ROD.isTagged(block.getType()) && config.getBoolean("enabled.endrod", true) ||
        				Tags.ANVIL.isTagged(block.getType()) && config.getBoolean("enabled.anvil", true) ||
        				Tags.CAMPFIRES.isTagged(block.getType()) && config.getBoolean("enabled.campfires", true) ||
        				Tags.SHULKER.isTagged(block.getType()) && config.getBoolean("enabled.shulker", true) ||
        				
                		Tags.HEADS2.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ) ) {
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
                event.setCancelled(true);
                event.setUseInteractedBlock(Result.DENY);
                state.setFacing(nextFace);
                block.setBlockData(state);
                BlockState state2 = block.getState();
                state2.update(true, true);
                World world = block.getWorld();
            	int X = block.getX();
            	int Y = block.getY();
            	int Z = block.getZ();
                if(world.getBlockAt(X+1, Y, Z).isEmpty()){
            		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);
            		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
            	}else if(world.getBlockAt(X-1, Y, Z).isEmpty()){
            		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
            		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
            	}else if(world.getBlockAt(X, Y, Z+1).isEmpty()){
            		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
            		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
            	}else if(world.getBlockAt(X, Y, Z-1).isEmpty()){
            		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
            		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
            	}
                //log("facing=" + facing);
                //log("nextFace=" + nextFace.toString());
            //}
        }else if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&
        		Tags.REDSTONE_COMPONENTS2.isTagged(block.getType()) && config.getBoolean("enabled.redstone", true) ){
        	Directional state = (Directional) block.getBlockData(); // Directional state = (Directional) block.getBlockData();
	        int facing = faces.indexOf(state.getFacing());
	        BlockFace nextFace = null;
	        int i = 0;
	        Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        event.setUseInteractedBlock(Result.DENY);
	        if(block.getType().equals(Material.REPEATER)){
	        	Repeater rep = (Repeater) block.getBlockData();
	        	int delay = rep.getDelay();
	        	Block block2 = block;
				BlockFace face = state.getFacing();
				world.getBlockAt(X, Y, Z).setType(Material.AIR);
				
				BlockData bd = Material.REPEATER.createBlockData();
				((Repeater) bd).setDelay(delay);
				while (nextFace == null || !state.getFaces().contains(nextFace)) {
		        	if (i >= 6) throw new IllegalStateException("Infinite loop detected");
		        		nextFace = event.getPlayer().isSneaking() ? facing - 1 < 0 ? faces.get(facing + 6 - 1) : faces.get(facing - 1) : faces.get((facing + 1) % 6); // 
		                facing = faces.indexOf(nextFace);
		                i++;
		        	}
				((Directional) bd).setFacing(nextFace);
				
				
				block2.setBlockData(bd);
				BlockState state2 = world.getBlockAt(X, Y, Z).getState();
				state2.update(true, true);
				RH.blockGrid2(world, X, Y, Z);
				return;
	        }
	        if(block.getType().equals(Material.COMPARATOR)){
	        	Comparator rep = (Comparator) block.getBlockData();
				Mode delay = rep.getMode();
	        	Block block2 = block;
				BlockFace face = state.getFacing();
				world.getBlockAt(X, Y, Z).setType(Material.AIR);
				
				BlockData bd = Material.COMPARATOR.createBlockData();
				((Comparator) bd).setMode(delay);
				while (nextFace == null || !state.getFaces().contains(nextFace)) {
		        	if (i >= 6) throw new IllegalStateException("Infinite loop detected");
		        		nextFace = event.getPlayer().isSneaking() ? facing - 1 < 0 ? faces.get(facing + 6 - 1) : faces.get(facing - 1) : faces.get((facing + 1) % 6); // 
		                facing = faces.indexOf(nextFace);
		                i++;
		        	}
				((Directional) bd).setFacing(nextFace);
				
				
				block2.setBlockData(bd);
				BlockState state2 = world.getBlockAt(X, Y, Z).getState();
				state2.update(true, true);
				RH.blockGrid2(world, X, Y, Z);
				return;
	        }
	        while (nextFace == null || !state.getFaces().contains(nextFace)) {
	        	if (i >= 6) throw new IllegalStateException("Infinite loop detected");
	        		nextFace = event.getPlayer().isSneaking() ? facing - 1 < 0 ? faces.get(facing + 6 - 1) : faces.get(facing - 1) : faces.get((facing + 1) % 6); // 
	                facing = faces.indexOf(nextFace);
	                i++;
	        	}
	        
	        state.setFacing(nextFace);
	        block.setBlockData(state);
	        BlockState state2 = block.getState();
	        state2.update(true, true);
	        RH.blockGrid2(world, X, Y, Z);
        }
        
        if(v1_15_R){
	        if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
	        		event.getItem().equals(wrench) && !onblacklist &&
	        		( com.github.joelgodofwar.rw.util.Tags_115.BEE.isTagged(block.getType()) && config.getBoolean("enabled.bee", true) ) ) {
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
        }
        
        /** Pistons */ //TODO: Pistons
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
            	event.getItem().equals(wrench) && !onblacklist &&
            	Tags.PISTONS.isTagged(block.getType()) && config.getBoolean("enabled.redstone", true) ){
        	Directional state = (Directional) block.getBlockData(); // Directional state = (Directional) block.getBlockData();
			BlockFace facing = state.getFacing();
	        BlockFace blockFace = null;
        	Piston piston = (Piston) block.getState().getBlockData();
			BlockFace face = piston.getFacing();
			Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        boolean ext = ((Piston) state).isExtended();
	        /**log("X-1=" + world.getBlockAt(X-1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X-1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()) ); // DISPENSER	East
	        log("Z-1=" + world.getBlockAt(X,Y,Z-1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z-1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z-1).getType()) ); // Hopper		South
	        log("X+1=" + world.getBlockAt(X+1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X+1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X+1,Y,Z).getType()) ); // Air			West
	        log("Z+1=" + world.getBlockAt(X,Y,Z+1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z+1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z+1).getType()) ); // stone 		North
	        log("Y-1=" + world.getBlockAt(X,Y-1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y-1,Z).getType()) ); // chest		Up
	        //log("block6=" + world.getBlockAt(X,Y+1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y+1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y+1,Z).getType()) ); // wc			Down*/
			switch(facing){
			case NORTH:
				if( RH.pistonValidBlock(world, X+1, Y, Z, ext) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.pistonValidBlock(world, X, Y, Z+1, ext) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.pistonValidBlock(world, X-1, Y, Z, ext) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.pistonValidBlock(world, X, Y-1, Z, ext) ){ // Up
		            blockFace = BlockFace.DOWN;
		        }else if ( RH.pistonValidBlock(world, X, Y+1, Z, ext) ){ // Down
		        	blockFace = BlockFace.UP;
		        }else if ( RH.pistonValidBlock(world, X, Y, Z-1, ext) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else {
		        	//log("north error");
		            return;
		        }
				break;
			case EAST:
				if ( RH.pistonValidBlock(world, X, Y, Z+1, ext) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.pistonValidBlock(world, X-1, Y, Z, ext) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.pistonValidBlock(world, X, Y-1, Z, ext) ){ // Up
		            blockFace = BlockFace.DOWN;
		        }else if ( RH.pistonValidBlock(world, X, Y+1, Z, ext) ){ // Down
		        	blockFace = BlockFace.UP;
		        }else if ( RH.pistonValidBlock(world, X, Y, Z-1, ext) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.pistonValidBlock(world, X+1, Y, Z, ext) ){ // North
		            blockFace = BlockFace.EAST;
				}else {
		        	//log("north error");
		            return;
		        }
				break;
			case SOUTH:
				if ( RH.pistonValidBlock(world, X-1, Y, Z, ext) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.pistonValidBlock(world, X, Y-1, Z, ext) ){ // Up
		            blockFace = BlockFace.DOWN;
		        }else if ( RH.pistonValidBlock(world, X, Y+1, Z, ext) ){ // Down
		        	blockFace = BlockFace.UP;
		        }else if ( RH.pistonValidBlock(world, X, Y, Z-1, ext) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.pistonValidBlock(world, X+1, Y, Z, ext) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.pistonValidBlock(world, X, Y, Z+1, ext) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else {
		        	//log("north error");
		            return;
		        }
				break;
			case WEST:
				if ( RH.pistonValidBlock(world, X, Y-1, Z, ext) ){ // Up
		            blockFace = BlockFace.DOWN;
		        }else if ( RH.pistonValidBlock(world, X, Y+1, Z, ext) ){ // Down
		        	blockFace = BlockFace.UP;
		        }else if ( RH.pistonValidBlock(world, X, Y, Z-1, ext) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.pistonValidBlock(world, X+1, Y, Z, ext) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.pistonValidBlock(world, X, Y, Z+1, ext) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.pistonValidBlock(world, X-1, Y, Z, ext) ){ // South
		            blockFace = BlockFace.WEST;
		        }else {
		        	//log("north error");
		            return;
		        }
				break;
			case DOWN:
				if ( RH.pistonValidBlock(world, X, Y+1, Z, ext) ){ // Down
		        	blockFace = BlockFace.UP;
		        }else if ( RH.pistonValidBlock(world, X, Y, Z-1, ext) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.pistonValidBlock(world, X+1, Y, Z, ext) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.pistonValidBlock(world, X, Y, Z+1, ext) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.pistonValidBlock(world, X-1, Y, Z, ext) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.pistonValidBlock(world, X, Y-1, Z, ext) ){ // Up
		            blockFace = BlockFace.DOWN;
		        }else {
		        	//log("north error");
		            return;
		        }
				break;
			case UP:
				if ( RH.pistonValidBlock(world, X, Y, Z-1, ext) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.pistonValidBlock(world, X+1, Y, Z, ext) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.pistonValidBlock(world, X, Y, Z+1, ext) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.pistonValidBlock(world, X-1, Y, Z, ext) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.pistonValidBlock(world, X, Y-1, Z, ext) ){ // Up
		            blockFace = BlockFace.DOWN;
		        }else if ( RH.pistonValidBlock(world, X, Y+1, Z, ext) ){ // Down
		        	blockFace = BlockFace.UP;
		        }else {
		        	//log("north error");
		            return;
		        }
				break;
			default:
				break;
			
			}
			//log("blockFace=" + blockFace);
	        event.setUseInteractedBlock(Result.DENY);
	        
	        ((Piston) state).setExtended(false);
	        state.setFacing(blockFace);
	        
	        block.setBlockData(state);
	        Directional state2 = (Directional) loc.getWorld().getBlockAt(loc).getBlockData();
	        ((Piston) state2).setExtended(ext);
	        loc.getWorld().getBlockAt(loc).setBlockData(state2);
	        loc.getWorld().getBlockAt(loc).getState().update(true, true);
	        world.getBlockAt(X-1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X+1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X, Y, Z-1).getState().update(true, true);
        	world.getBlockAt(X, Y, Z+1).getState().update(true, true);
        	world.getBlockAt(X, Y-1, Z).getState().update(true, true);
        	world.getBlockAt(X, Y+1, Z).getState().update(true, true);
        	if(world.getBlockAt(X+1, Y, Z).isEmpty()){
        		world.getBlockAt(X+1, Y, Z).setType(Material.REDSTONE_BLOCK);
        		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X-1, Y, Z).isEmpty()){
        		world.getBlockAt(X-1, Y, Z).setType(Material.REDSTONE_BLOCK);
        		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z+1).isEmpty()){
        		world.getBlockAt(X, Y, Z+1).setType(Material.REDSTONE_BLOCK);
        		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z-1).isEmpty()){
        		world.getBlockAt(X, Y, Z-1).setType(Material.REDSTONE_BLOCK);
        		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y+1, Z).isEmpty()){
        		world.getBlockAt(X, Y+1, Z).setType(Material.REDSTONE_BLOCK);
        		world.getBlockAt(X, Y+1, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y-1, Z).isEmpty()){
        		world.getBlockAt(X, Y-1, Z).setType(Material.REDSTONE_BLOCK);
        		world.getBlockAt(X, Y-1, Z).setType(Material.AIR);
        	}
	        //((BlockState) state2).update(true, true);
	        //block.getRelative(arg0, arg1)
	        
        }
        
        /** Lantern */ // TODO: Lantern
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		Tags.LANTERNS.isTagged(block.getType()) && config.getBoolean("enabled.lantern", true) ){ 
        	//Tags.WORKSTATIONS.isTagged(block.getType()) && config.getBoolean("enabled.workstations", true)
        	BlockState state = block.getState();
        	Lantern lantern = (Lantern) state.getBlockData();
        	boolean hanging = lantern.isHanging();
        	//log("Lantern clicked");
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        if(!hanging && 
	        		RH.lanternValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // RH.lanternValidBlock(world, X, Y+1, Z, Half.BOTTOM)
	        	lantern.setHanging(true);
	        	state.setBlockData(lantern);
	        	//log("Lantern set hanging");
	        }else if(hanging && 
	        		RH.lanternValidBlock(world, X, Y-1, Z, Half.TOP) ){
	        	lantern.setHanging(false);
	        	state.setBlockData(lantern);
	        	//log("Lantern set not hanging");
	        }
	        state.update(true, true);
        }
        
        /** Ladder */ // TODO: Ladder & Tripwire Hook
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		( block.getType() == Material.LADDER && config.getBoolean("enabled.ladder", true) ||
        			block.getType() == Material.TRIPWIRE_HOOK && config.getBoolean("enabled.redstone", true) ) ){ 
        	//Tags.WORKSTATIONS.isTagged(block.getType()) && config.getBoolean("enabled.workstations", true)
        	BlockState state = block.getState();
        	Directional face = (Directional) state.getBlockData();
        	BlockFace facing = face.getFacing();
        	
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        BlockFace blockFace = null;
	        switch(facing){
			case NORTH:
				/**log("empty=" + !world.getBlockAt(X,Y-1,Z).isEmpty());
				log("solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid());
				log("tagged=" + !Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()));*/
				if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
					
		            blockFace = BlockFace.EAST;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST;
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH;
		        }else {
		        	//log("north error");
		            return;
		        }
				//log("blockFace=" + blockFace);
				break;
			case EAST:
				if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH; //log("south error");
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST; //log("west error");
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH; //log("north error");
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST; //log("east error");
				}else {
		            return;
		        }
				break;
			case SOUTH:
				if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
					
		            blockFace = BlockFace.WEST;
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else {
		            return;
		        }
				break;
			case WEST:
				if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
					
		            blockFace=BlockFace.NORTH;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST;
		        }else {
		            return;
		        }
				break;
			case UP:
				if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST;
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST;
				}else {
		            return;
		        }
				break;
			default:
				//log("error facing=" + facing);
				break;
        	}
        	//log("blockFace=" + blockFace);
	        face.setFacing(blockFace);
        	state.setBlockData(face);
        	state.update(true, true);
	        
	        
        }
        
        /** Torches */ // TODO: Torches
        if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        	  ( Tags.TORCH.isTagged(block.getType()) && config.getBoolean("enabled.torch", true) ) ){
        	
        	/**if(block.getType().equals(Material.SOUL_TORCH)){
        		BlockFace blockFace = null;
        		BlockData data = Material.SOUL_WALL_TORCH.createBlockData();
        		Directional state = (Directional) data;
        		Location loc = block.getLocation();
            	World world = loc.getWorld();
    	        int X = loc.getBlockX();
    	        int Y = loc.getBlockY();
    	        int Z = loc.getBlockZ();

        		if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
        			
		            blockFace=BlockFace.SOUTH; //log("south error");
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST; //log("west error");
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH; //log("north error");
		        }else if ( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // South
		        	
		            blockFace = BlockFace.EAST; //log("west error");
		        }else {
		            return;
		        }
        		state.setFacing(blockFace);
        		block.setBlockData(state);
        		return;
        	}else if(block.getType().equals(Material.SOUL_WALL_TORCH) && RH.torchValidBlock2(block.getWorld(), block.getX(), (block.getY()-1), block.getZ(), Half.TOP) ){
        		if(debug){logDebug("y=" + block.getY() + " y-1=" + (block.getY()-1));}
        		block.setType(Material.SOUL_TORCH);
                return;
        	}else*/ if(block.getType().equals(Material.TORCH)){
        		BlockFace blockFace = null;
        		BlockData data = Material.WALL_TORCH.createBlockData();
        		Directional state = (Directional) data;
        		Location loc = block.getLocation();
            	World world = loc.getWorld();
    	        int X = loc.getBlockX();
    	        int Y = loc.getBlockY();
    	        int Z = loc.getBlockZ();

        		if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
        			
		            blockFace=BlockFace.SOUTH; //log("south error");
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST; //log("west error");
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH; //log("north error");
		        }else if ( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // South
		        	
		            blockFace = BlockFace.EAST; //log("west error");
		        }else {
		            return;
		        }
        		state.setFacing(blockFace);
        		block.setBlockData(state);
        		return;
        	}else if(block.getType().equals(Material.WALL_TORCH) && RH.torchValidBlock2(block.getWorld(), block.getX(), (block.getY()-1), block.getZ(), Half.TOP) ){
        		block.setType(Material.TORCH);
                return;
        	}else if(block.getType().equals(Material.REDSTONE_TORCH)){
        		BlockFace blockFace = null;
        		BlockData data = Material.REDSTONE_WALL_TORCH.createBlockData();
        		Directional state = (Directional) data;
        		Location loc = block.getLocation();
            	World world = loc.getWorld();
    	        int X = loc.getBlockX();
    	        int Y = loc.getBlockY();
    	        int Z = loc.getBlockZ();

        		if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
        			
		            blockFace=BlockFace.SOUTH; //log("south error");
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST; //log("west error");
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH; //log("north error");
		        }else if ( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // South
		        	
		            blockFace = BlockFace.EAST; //log("west error");
		        }else {
		            return;
		        }
        		state.setFacing(blockFace);
        		block.setBlockData(state);
        		return;
        	}else if(block.getType().equals(Material.REDSTONE_WALL_TORCH) && RH.torchValidBlock2(block.getWorld(), block.getX(), (block.getY()-1), block.getZ(), Half.TOP) ){
        		block.setType(Material.REDSTONE_TORCH);
                return;
        	}
        	
        }else if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		( Tags.TORCH.isTagged(block.getType()) && config.getBoolean("enabled.torch", true) ) ){
        	event.setUseInteractedBlock(Result.DENY);
        	//log("Material=" + block.getType().toString());
        	if(block.getType().equals(Material.TORCH)||block.getType().equals(Material.REDSTONE_TORCH)){return;}
        	Directional state = (Directional) block.getBlockData();
        	BlockFace facing = state.getFacing();
        	BlockFace blockFace = null;
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	
	        int X2 = X;
	        int Y2 = Y;
	        int Z2 = Z;
	        //block.getType().isSolid()
	        /**log("X-1=" + world.getBlockAt(X-1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X-1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()) ); // DISPENSER	East
	        log("Z-1=" + world.getBlockAt(X,Y,Z-1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z-1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z-1).getType()) ); // Hopper		South
	        log("X+1=" + world.getBlockAt(X+1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X+1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X+1,Y,Z).getType()) ); // Air			West
	        log("Z+1=" + world.getBlockAt(X,Y,Z+1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z+1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z+1).getType()) ); // stone 		North
	        log("Y-1=" + world.getBlockAt(X,Y-1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y-1,Z).getType()) ); // chest		Up
	        //log("block6=" + world.getBlockAt(X,Y+1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y+1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y+1,Z).getType()) ); // wc			Down*/
	        //log("tagged=" + Tags.NO_BUTTONS.isTagged(block.getType()));
	        
        	switch(facing){
			case NORTH:
				/**log("empty=" + !world.getBlockAt(X,Y-1,Z).isEmpty());
				log("solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid());
				log("tagged=" + !Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()));*/
				if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else {
		        	//log("north error");
		            return;
		        }
				//log("blockFace=" + blockFace);
				break;
			case EAST:
				if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH; //log("south error");
		        }else if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST; //log("west error");
		        }else if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH; //log("north error");
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST; //log("east error");
				}else {
		            return;
		        }
				break;
			case SOUTH:
				if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
				}else if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else {
		            return;
		        }
				break;
			case WEST:
				if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
				}else if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		        }else {
		            return;
		        }
				break;
			case UP:
				if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
				}else {
		            return;
		        }
				break;
			default:
				//log("error facing=" + facing);
				break;
        	}
        	//log("blockFace=" + blockFace);
        	state.setFacing(blockFace);
        	block.setBlockData(state);
            BlockState state2 = block.getState();
            state2.update(true, true);
        }
        
        /** Bell */ // TODO: Bell
        if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		block.getType().equals(Material.BELL) && config.getBoolean("enabled.redstone", true) ){
        	//logDebug("Bell clicked");
        	event.setUseInteractedBlock(Result.DENY);
        	BlockState state = block.getState();
        	BlockData data = state.getBlockData(); // Intersection type of two interfaces
        	BlockFace facing = ((Directional) data).getFacing();
        	Bell bell = (Bell) state.getBlockData();
        	Attachment aface = bell.getAttachment();
        	/**log("facing=" + facing);
        	log("aface=" + aface); //*/
        	Attachment Face = null; /** */
        	BlockFace blockFace = null;
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	
	        //block.getType().isSolid()
	        /**log("block1=" + world.getBlockAt(X-1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X-1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()) ); // DISPENSER	East
	        log("block2=" + world.getBlockAt(X,Y,Z-1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z-1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z-1).getType()) ); // Hopper		South
	        log("block3=" + world.getBlockAt(X+1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X+1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X+1,Y,Z).getType()) ); // Air			West
	        log("block4=" + world.getBlockAt(X,Y,Z+1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z+1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z+1).getType()) ); // stone 		North
	        log("block5=" + world.getBlockAt(X,Y-1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y-1,Z).getType()) ); // chest		Up
	        log("block6=" + world.getBlockAt(X,Y+1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y+1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y+1,Z).getType()) ); // wc			Down*/
	        //log("tagged=" + Tags.NO_BUTTONS.isTagged(block.getType()));
	        switch(aface){
	        case CEILING:
	        	switch(facing){
				case NORTH:
					blockFace = BlockFace.EAST;
		            Face = Attachment.CEILING;
		        	break;
				case EAST:
		            blockFace=BlockFace.SOUTH;
		            Face = Attachment.CEILING;
		        	break;
				case SOUTH:
		            blockFace = BlockFace.WEST;
		            Face = Attachment.CEILING;
		        	break;
				case WEST:
		            blockFace=BlockFace.NORTH;
		            Face = Attachment.CEILING;
		        	break;
	        	}
	        	break;
	        case FLOOR:
	        	switch(facing){
				case NORTH:
					blockFace = BlockFace.EAST;
		            Face = Attachment.FLOOR;
		        	break;
				case EAST:
		            blockFace=BlockFace.SOUTH;
		            Face = Attachment.FLOOR;
		        	break;
				case SOUTH:
		            blockFace = BlockFace.WEST;
		            Face = Attachment.FLOOR;
		        	break;
				case WEST:
		            blockFace=BlockFace.NORTH;
		            Face = Attachment.FLOOR;
		        	break;
	        	}
	        	break;
	        case SINGLE_WALL:
	        case DOUBLE_WALL:
	        	switch(facing){
				case NORTH:
					if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
					}else if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else {
			        	log("Bell north wall error");
			            return;
			        }
					break;
				case EAST:
					if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
					}else if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else {
						log("Bell east wall error");
			            return;
			        }
					break;
				case SOUTH:
					if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
					}else if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else {
			        	log("Bell south wall error");
			            return;
			        }
					break;
				case WEST:
					if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            if( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            if( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){
			            	Face = Attachment.DOUBLE_WALL;
			            }else{
			            	Face = Attachment.SINGLE_WALL;
			            }
					}else {
			        	log("Bell west wall error");
			            return;
			        }
					break;

				default:
					log("default error");
					break;
	        	}
        	
	        	break;
	        }
	        if(Face != null){
	        	bell.setFacing(blockFace);
	        	bell.setAttachment(Face);
	        	state.setBlockData(bell);
	        	state.update(true, true);
	        }else{
	        	((Directional) data).setFacing(blockFace);
	        	state.setBlockData(data);
	        	state.update(true, true);
	        }
	        if(world.getBlockAt(X+1, Y, Z).isEmpty()){
        		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
        	}
	        if(world.getBlockAt(X-1, Y, Z).isEmpty()){
        		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
        	}
        	if(world.getBlockAt(X, Y, Z+1).isEmpty()){
        		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
        	}
        	if(world.getBlockAt(X, Y, Z-1).isEmpty()){
        		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
        	}
	        if(world.getBlockAt(X, Y+1, Z).isEmpty()){
        		world.getBlockAt(X, Y+1, Z).setType(Material.BARRIER);
        		world.getBlockAt(X, Y+1, Z).setType(Material.AIR);
        	}
	        if(world.getBlockAt(X, Y-1, Z).isEmpty()){
        		world.getBlockAt(X, Y-1, Z).setType(Material.BARRIER);
        		world.getBlockAt(X, Y-1, Z).setType(Material.AIR);
        	}
	        world.getBlockAt(X-1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X+1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X, Y, Z-1).getState().update(true, true);
        	world.getBlockAt(X, Y, Z+1).getState().update(true, true);
        	world.getBlockAt(X, Y-1, Z).getState().update(true, true);
        	world.getBlockAt(X, Y+1, Z).getState().update(true, true);
        }else if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		block.getType().equals(Material.BELL) && config.getBoolean("enabled.redstone", true) ){
        	event.setUseInteractedBlock(Result.DENY);
        	BlockState state = block.getState();
        	BlockData data = state.getBlockData(); // Intersection type of two interfaces
        	BlockFace facing = ((Directional) data).getFacing();
        	Bell bell = (Bell) state.getBlockData();
        	Attachment aface = bell.getAttachment();
        	/**log("facing=" + facing);
        	log("aface=" + aface); //*/
        	Attachment Face = null; /** */
        	BlockFace blockFace = null;
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        
	        /**log("block1=" + world.getBlockAt(X-1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X-1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()) ); // DISPENSER	East
	        log("block2=" + world.getBlockAt(X,Y,Z-1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z-1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z-1).getType()) ); // Hopper		South
	        log("block3=" + world.getBlockAt(X+1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X+1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X+1,Y,Z).getType()) ); // Air			West
	        log("block4=" + world.getBlockAt(X,Y,Z+1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z+1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z+1).getType()) ); // stone 		North
	        log("block5=" + world.getBlockAt(X,Y-1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y-1,Z).getType()) ); // chest		Up
	        log("block6=" + world.getBlockAt(X,Y+1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y+1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y+1,Z).getType()) ); // wc			Down*/
	        //log("tagged=" + Tags.NO_BUTTONS.isTagged(block.getType()));
	        
	        switch(aface){
	        case CEILING:
	        	if( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){
	        		blockFace = facing;
		        	Face = Attachment.FLOOR;
	        	}else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.EAST;
		            Face = Attachment.DOUBLE_WALL;
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.SOUTH;
		            Face = Attachment.DOUBLE_WALL;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.WEST;
		            Face = Attachment.DOUBLE_WALL;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.NORTH;
		            Face = Attachment.DOUBLE_WALL;
		        }else {
		        	log("Bell north wall error");
		            return;
		        }
	        	break;
	        case FLOOR:
	        	switch(facing){
				case NORTH:
					if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else{
			        	log("Bell north wall error");
			            return;
			        }
					break;
				case EAST:
					if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else{
						log("Bell east wall error");
			            return;
			        }
					break;
				case SOUTH:
					if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else{
			        	log("Bell south wall error");
			            return;
			        }
					break;
				case WEST:
					if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else{
			        	log("Bell west wall error");
			            return;
			        }
					break;

				default:
					log("default error");
					break;
	        	}
	        	break;
	        case SINGLE_WALL:
	        case DOUBLE_WALL:
	        	//log("aface=" + aface.toString());
	        	//log("facing=" + facing.toString());
	        	switch(facing){
	        	case NORTH:
	        		if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else if( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){
		        		blockFace = facing;
			        	Face = Attachment.FLOOR;
		        	}else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else {
			        	log("Bell north wall error");
			            return;
			        }
					break;
				case EAST:
					if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else if( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){
		        		blockFace = facing;
			        	Face = Attachment.FLOOR;
		        	}else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else {
						log("Bell east wall error");
			            return;
			        }
					break;
				case SOUTH:
					if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else if( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){
		        		blockFace = facing;
			        	Face = Attachment.FLOOR;
		        	}else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else {
			        	log("Bell south wall error");
			            return;
			        }
					break;
				case WEST:
					if( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){
		        		blockFace = facing;
			        	Face = Attachment.CEILING;
		        	}else if( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){
		        		blockFace = facing;
			        	Face = Attachment.FLOOR;
		        	}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.NORTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.EAST;
			            Face = Attachment.DOUBLE_WALL;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.SOUTH;
			            Face = Attachment.DOUBLE_WALL;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.WEST;
			            Face = Attachment.DOUBLE_WALL;
					}else {
			        	log("Bell west wall error");
			            return;
			        }
					break;

				default:
					log("default error");
					break;
	        	}
        	
	        	break;
	        }
	        if(Face != null){
	        	bell.setFacing(blockFace);
	        	bell.setAttachment(Face);
	        	state.setBlockData(bell);
	        	state.update(true, true);
	        }else{
	        	((Directional) data).setFacing(blockFace);
	        	state.setBlockData(data);
	        	state.update(true, true);
	        }
	        if(world.getBlockAt(X+1, Y, Z).isEmpty()){
        		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
        	}
	        if(world.getBlockAt(X-1, Y, Z).isEmpty()){
        		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
        	}
        	if(world.getBlockAt(X, Y, Z+1).isEmpty()){
        		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
        	}
        	if(world.getBlockAt(X, Y, Z-1).isEmpty()){
        		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
        	}
	        if(world.getBlockAt(X, Y+1, Z).isEmpty()){
        		world.getBlockAt(X, Y+1, Z).setType(Material.BARRIER);
        		world.getBlockAt(X, Y+1, Z).setType(Material.AIR);
        	}
	        if(world.getBlockAt(X, Y-1, Z).isEmpty()){
        		world.getBlockAt(X, Y-1, Z).setType(Material.BARRIER);
        		world.getBlockAt(X, Y-1, Z).setType(Material.AIR);
        	}
	        world.getBlockAt(X-1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X+1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X, Y, Z-1).getState().update(true, true);
        	world.getBlockAt(X, Y, Z+1).getState().update(true, true);
        	world.getBlockAt(X, Y-1, Z).getState().update(true, true);
        	world.getBlockAt(X, Y+1, Z).getState().update(true, true);
        }
        
        
        /** Buttons */ // TODO: Buttons
        if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		Tags.BUTTONS.isTagged(block.getType()) && config.getBoolean("enabled.buttons", true) ){
        	event.setUseInteractedBlock(Result.DENY);
        	BlockState state = block.getState();
        	BlockData data = state.getBlockData(); // Intersection type of two interfaces
        	BlockFace facing = ((Directional) data).getFacing();
        	Face aface = ((Switch) data).getFace();
        	
        	Face Face = null;
        	BlockFace blockFace = null;
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	
	        int X2 = X;
	        int Y2 = Y;
	        int Z2 = Z;
	        //block.getType().isSolid()
	        /**log("block1=" + world.getBlockAt(X-1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X-1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()) ); // DISPENSER	East
	        log("block2=" + world.getBlockAt(X,Y,Z-1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z-1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z-1).getType()) ); // Hopper		South
	        log("block3=" + world.getBlockAt(X+1,Y,Z).getType().toString() + " solid=" + world.getBlockAt(X+1,Y,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X+1,Y,Z).getType()) ); // Air			West
	        log("block4=" + world.getBlockAt(X,Y,Z+1).getType().toString() + " solid=" + world.getBlockAt(X,Y,Z+1).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z+1).getType()) ); // stone 		North
	        log("block5=" + world.getBlockAt(X,Y-1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y-1,Z).getType()) ); // chest		Up
	        log("block6=" + world.getBlockAt(X,Y+1,Z).getType().toString() + " solid=" + world.getBlockAt(X,Y+1,Z).getType().isSolid() + " tagged=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y+1,Z).getType()) ); // wc			Down*/
	        //log("tagged=" + Tags.NO_BUTTONS.isTagged(block.getType()));
	        switch(aface){
	        case CEILING:
	        	if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		            Face = Face.WALL;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
		            Face = Face.WALL;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		            Face = Face.WALL;
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		            Face = Face.WALL;
		        }else if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
		            blockFace = facing; // floor
		            Face = Face.FLOOR;
		        }else if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
		        	blockFace = facing; // ceiling
		            Face = Face.CEILING;
		        }else {
		        	//log("north error");
		            return;
		        }
	        	break;
	        case FLOOR:
	        	if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
		        	blockFace = facing; // ceiling
		            Face = Face.CEILING;
		        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		            Face = Face.WALL;
		        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
		            Face = Face.WALL;
				}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		            Face = Face.WALL;
		        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		            Face = Face.WALL;
		        }else {
		        	//log("north error");
		            return;
		        }
	        	break;
	        case WALL:
	        	switch(facing){
				case NORTH:
					/**log("empty=" + !world.getBlockAt(X,Y-1,Z).isEmpty());
					log("solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid());
					log("tagged=" + !Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()));*/
					if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.EAST;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.SOUTH;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.WEST;
			        }else if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
			            blockFace = facing; // floor
			            Face = Face.FLOOR;
			        }else if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
			        	blockFace = facing; // ceiling
			            Face = Face.CEILING;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.NORTH;
			        }else {
			        	//log("north error");
			            return;
			        }
					//log("blockFace=" + blockFace);
					break;
				case EAST:
					if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.SOUTH;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.WEST;
			        }else if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
			        	blockFace = facing; // floor
			            Face = Face.FLOOR;
			        }else if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
			        	blockFace = facing; // ceiling
			            Face = Face.CEILING;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.NORTH;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.EAST;
					}else {
			            return;
			        }
					break;
				case SOUTH:
					if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.WEST;
			        }else if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
			        	blockFace = facing; // floor
			            Face = Face.FLOOR;
			        }else if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
			        	blockFace = facing; // ceiling
			            Face = Face.CEILING;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.NORTH;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.EAST;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.SOUTH;
			        }else {
			            return;
			        }
					break;
				case WEST:
					if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
						blockFace = facing; // floor
			            Face = Face.FLOOR;
			        }else if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
			        	blockFace = facing; // ceiling
			            Face = Face.CEILING;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.NORTH;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.EAST;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.SOUTH;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.WEST;
			        }else {
			            return;
			        }
					break;
				case UP:
					if ( RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
						blockFace = facing; // ceiling
			            Face = Face.CEILING;
			        }else if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.NORTH;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.EAST;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.SOUTH;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.WEST;
			        }else if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
			        	blockFace = facing; // floor
			            Face = Face.FLOOR;
			        }else {
			            return;
			        }
					break;
				case DOWN:
					if ( RH.torchValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
			            blockFace=BlockFace.NORTH;
			        }else if( RH.torchValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
			            blockFace = BlockFace.EAST;
					}else if ( RH.torchValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
			            blockFace=BlockFace.SOUTH;
			        }else if ( RH.torchValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
			            blockFace = BlockFace.WEST;
			        }else if ( RH.buttonValidBlock(world, X, Y-1, Z, Half.TOP) ){ // Up
			        	blockFace = facing; // floor
			            Face = Face.FLOOR;
			        }else if (! RH.buttonValidBlock(world, X, Y+1, Z, Half.BOTTOM) ){ // Down
			        	blockFace = facing; // ceiling
			            Face = Face.CEILING;
			        }else {
			            return;
			        }
					break;
				default:
					//log("error facing=" + facing);
					break;
	        	}
        	
	        	break;
	        }
	        if(Face != null){
	        	//log("Face=" + Face);
	        	((Directional) data).setFacing(blockFace);
	        	((Switch) data).setFace(Face);
	        	state.setBlockData(data);
	        	state.update(true, true);
	        }else{
	        	//log("Face == null");
	        	((Directional) data).setFacing(blockFace);
	        	state.setBlockData(data);
	        	state.update(true, true);
	        }
	        RH.blockGrid(world, X, Y, Z);
        	
        }else if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		Tags.BUTTONS.isTagged(block.getType()) && config.getBoolean("enabled.buttons", true) ){
        	event.setUseInteractedBlock(Result.DENY);
        	BlockState state = block.getState();
        	Directional wallButton = (Directional) state.getBlockData(); // Wall Button
        	Switch bFace = (Switch) state.getBlockData();
        	//FaceAttachable faceButton = (FaceAttachable) state.getBlockData(); // Floor Ceiling Button
        	BlockFace facing = wallButton.getFacing();
        	Face aface = bFace.getFace();
        	//log("facing=" + facing);
        	//log("aface=" + aface);
        	//Face Face = null;
        	BlockFace blockFace = null;
        	Location loc = block.getLocation();
        	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        
	        if(aface.equals(Face.WALL)){
	        	return;
	        }
	        
	        switch(facing){
			case NORTH:
		            blockFace = BlockFace.EAST;
				break;
			case EAST:
		            blockFace=BlockFace.SOUTH;
				break;
			case SOUTH:
		            blockFace = BlockFace.WEST;
				break;
			case WEST:
		            blockFace=BlockFace.NORTH;
				break;
			default:
				//log("error facing=" + facing);
				break;
        	}
	        wallButton.setFacing(blockFace);
        	state.setBlockData(wallButton);
	    	state.update(true, true);
        }
        
        /** Chests */
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
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
    		if(!block.getType().equals(Material.ENDER_CHEST)){
    			if( !(((org.bukkit.block.Chest)block.getState()).getInventory() instanceof DoubleChestInventory) ) {
	    			Directional dir = (Directional)block.getBlockData();
	    			if(dir.getFacing() == BlockFace.EAST) dir.setFacing(BlockFace.NORTH);
	    			else if(dir.getFacing() == BlockFace.NORTH) dir.setFacing(BlockFace.WEST);
	    			else if(dir.getFacing() == BlockFace.WEST) dir.setFacing(BlockFace.SOUTH);
	    			else if(dir.getFacing() == BlockFace.SOUTH) dir.setFacing(BlockFace.EAST);
	    			block.setBlockData(dir);
        		}
			}else if(block.getType().equals(Material.ENDER_CHEST)){
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
        
        /** Heads */ //skullfaces TODO: Heads
        if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		( Tags.HEADS.isTagged(block.getType()) && config.getBoolean("enabled.heads", true) ||
        			Tags.HEADS2.isTagged(block.getType()) && config.getBoolean("enabled.heads", true) ) ){
        	BlockState state = block.getState();
			Material type = block.getType();
			switch(type){
			case PLAYER_HEAD:
				type = Material.PLAYER_WALL_HEAD;
				break;
			case CREEPER_HEAD:
				type = Material.CREEPER_WALL_HEAD;
				break;
			case ZOMBIE_HEAD:
				type = Material.ZOMBIE_WALL_HEAD;
				break;
			case SKELETON_SKULL:
				type = Material.SKELETON_WALL_SKULL;
				break;
			case WITHER_SKELETON_SKULL:
				type = Material.WITHER_SKELETON_WALL_SKULL;
				break;
			case DRAGON_HEAD:
				type = Material.DRAGON_WALL_HEAD;
				break;
			case PLAYER_WALL_HEAD:
				type = Material.PLAYER_HEAD;
				break;
			case CREEPER_WALL_HEAD:
				type = Material.CREEPER_HEAD;
				break;
			case ZOMBIE_WALL_HEAD:
				type = Material.ZOMBIE_HEAD;
				break;
			case SKELETON_WALL_SKULL:
				type = Material.SKELETON_SKULL;
				break;
			case WITHER_SKELETON_WALL_SKULL:
				type = Material.WITHER_SKELETON_SKULL;
				break;
			case DRAGON_WALL_HEAD:
				type = Material.DRAGON_HEAD;
				break;
			default:
				break;
			}
			state.setType(type);
			state.update(true, true);
        }else if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		Tags.HEADS.isTagged(block.getType()) && config.getBoolean("enabled.heads", true) ){
        	BlockState state = block.getState();
        	BlockFace blockFace = null;
        	BlockFace rotation = null;
        	Skull skull = null;
        	
	        skull = (Skull) block.getState();
	        rotation = skull.getRotation();
        	switch(rotation){
        	case NORTH: 			// 1 
           	 		blockFace =  BlockFace.NORTH_NORTH_EAST;
        		break;
        	case NORTH_NORTH_EAST: 	// 2 
           	 		blockFace =  BlockFace.NORTH_EAST;
        		break;
        	case NORTH_EAST: 		// 3 
           	 		blockFace =  BlockFace.EAST_NORTH_EAST;
        		break;
        	case EAST_NORTH_EAST: 	// 4 
           	 		blockFace =  BlockFace.EAST;
        		break;
        	case EAST: 				// 5 
           	 		blockFace =  BlockFace.EAST_SOUTH_EAST;
        		break;
        	case EAST_SOUTH_EAST: 	// 6 
           	 		blockFace =  BlockFace.SOUTH_EAST;
        		break;
        	case SOUTH_EAST: 		// 7 
           	 		blockFace =  BlockFace.SOUTH_SOUTH_EAST;
        		break;
        	case SOUTH_SOUTH_EAST: 	// 8 
           	 		blockFace =  BlockFace.SOUTH;
        		break;
        	case SOUTH: 			// 9 
           	 		blockFace =  BlockFace.SOUTH_SOUTH_WEST;
        		break;
        	case SOUTH_SOUTH_WEST: 	// 10 
           	 		blockFace =  BlockFace.SOUTH_WEST;
        		break;
        	case SOUTH_WEST: 		// 11 
           	 		blockFace =  BlockFace.WEST_SOUTH_WEST;
           	 		break;
        	case WEST_SOUTH_WEST: 	// 12 
           	 		blockFace =  BlockFace.WEST;
        		break;
        	case WEST: 				// 13 
           	 		blockFace =  BlockFace.WEST_NORTH_WEST;
        		break;
        	case WEST_NORTH_WEST: 	// 14 
           	 		blockFace =  BlockFace.NORTH_WEST;
        		break;
        	case NORTH_WEST: 		// 15 
           	 		blockFace =  BlockFace.NORTH_NORTH_WEST;
        		break;
        	case NORTH_NORTH_WEST: 	// 16 blockFace =  BlockFace.NORTH_NORTH_WEST;
           	 		blockFace =  BlockFace.NORTH;
        		break;
			default:
				break;
        	}
        	skull.setRotation(blockFace);
        	skull.update(true, true);

        	//block.setBlockData(skull);
            event.setUseInteractedBlock(Result.DENY);
        }
        
        /** Signs */ //skullfaces TODO: Signs
        if( event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		( Tags_116.SIGNS.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ||
        			Tags_116.SIGNS2.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ||
        			Tags_116.BANNER.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ||
        			Tags_116.WALL_BANNER.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ) ){
        	BlockState state = block.getState();
        	World world = block.getWorld();
        	int X = block.getX();
        	int Y = block.getY();
        	int Z = block.getZ();
        	BlockFace blockFace = null;
        	if( Tags_116.SIGNS.isTagged(block.getType()) || Tags_116.BANNER.isTagged(block.getType()) ){
        		//log("SB is SB");
        		if( RH.signValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
    	            blockFace = BlockFace.EAST;
    			}else if ( RH.signValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
    	            blockFace=BlockFace.SOUTH;
    	        }else if ( RH.signValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
    	            blockFace = BlockFace.WEST;
    	        }else if ( RH.signValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
    	            blockFace=BlockFace.NORTH;
    	        }else{
    	        	return;
    	        }
        		BlockData mat = Material.getMaterial(RH.getOpposite(block.getType()).toString()).createBlockData();
        		((Directional) mat).setFacing(blockFace);
        		state.setBlockData(mat);
        		//state.setType(getOpposite(block.getType()));
        		state.update(true, true);
        		//block.setType(getOpposite(block.getType()));
				//BlockState state2 = world.getBlockAt(X, Y, Z).getState();
        		//((Directional) state2).setFacing(blockFace);
        		//state2.update(true, true);
        	}else if( Tags_116.SIGNS2.isTagged(block.getType()) || Tags_116.WALL_BANNER.isTagged(block.getType()) ){
        		if( RH.signValidBlock2(world, X, Y-1, Z, Half.TOP) ){ // North
        			Directional sign = (Directional) block.getBlockData();
        			blockFace = sign.getFacing();
        			BlockData mat = Material.getMaterial(RH.getOpposite(block.getType()).toString()).createBlockData();
            		((Rotatable) mat).setRotation(blockFace);
            		state.setBlockData(mat);
            		state.update(true, true);
    			}
        	}else{
        		log("SB Error");
        	}
        	world.getBlockAt(X-1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X+1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X, Y, Z-1).getState().update(true, true);
        	world.getBlockAt(X, Y, Z+1).getState().update(true, true);
        	world.getBlockAt(X, Y-1, Z).getState().update(true, true);
        	world.getBlockAt(X, Y+1, Z).getState().update(true, true);
        }else if( !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		( Tags_116.SIGNS.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ||
        				Tags_116.BANNER.isTagged(block.getType()) && config.getBoolean("enabled.banner", true) ) ){
        	BlockState state = block.getState();
        	Rotatable sign = (Rotatable) block.getBlockData();
        	//BlockState state = block.getState();
        	BlockFace rotation = sign.getRotation();
        	BlockFace blockFace = null;
        	//log("rotation=" + rotation);
        	World world = block.getWorld();
        	int X = block.getX();
        	int Y = block.getY();
        	int Z = block.getZ();
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
        	sign.setRotation(blockFace);
        	state.setBlockData(sign);
        	state.update(true, true);
        	//BlockState state2 = block.getState();
            //state2.update(true, true);
        		//sign.update(true, true);
        	
        	//block.setBlockData(skull);
            event.setUseInteractedBlock(Result.DENY);
            world.getBlockAt(X-1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X+1, Y, Z).getState().update(true, true);
        	world.getBlockAt(X, Y, Z-1).getState().update(true, true);
        	world.getBlockAt(X, Y, Z+1).getState().update(true, true);
        	world.getBlockAt(X, Y-1, Z).getState().update(true, true);
        	world.getBlockAt(X, Y+1, Z).getState().update(true, true);
        	if(world.getBlockAt(X+1, Y, Z).isEmpty()){
        		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X-1, Y, Z).isEmpty()){
        		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z+1).isEmpty()){
        		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z-1).isEmpty()){
        		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y+1, Z).isEmpty()){
        		world.getBlockAt(X, Y+1, Z).setType(Material.BARRIER);
        		world.getBlockAt(X, Y+1, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y-1, Z).isEmpty()){
        		world.getBlockAt(X, Y-1, Z).setType(Material.BARRIER);
        		world.getBlockAt(X, Y-1, Z).setType(Material.AIR);
        	}
        }else
        	/** Wall Sign */ // TODO: Wall Sign
            if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
    		event.getItem().equals(wrench) && !onblacklist &&  
    		( Tags_116.SIGNS2.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ||
			Tags_116.WALL_BANNER.isTagged(block.getType()) && config.getBoolean("enabled.signs", true) ) ){ 
	    	//Tags_116.WORKSTATIONS.isTagged(block.getType()) && config.getBoolean("enabled.workstations", true)
	    	BlockState state = block.getState();
	    	Directional face = (Directional) state.getBlockData();
	    	BlockFace facing = face.getFacing();
	    	
	    	Location loc = block.getLocation();
	    	World world = loc.getWorld();
	        int X = loc.getBlockX();
	        int Y = loc.getBlockY();
	        int Z = loc.getBlockZ();
	        BlockFace blockFace = null;
	        switch(facing){
			case NORTH:
				/**log("empty=" + !world.getBlockAt(X,Y-1,Z).isEmpty());
				log("solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid());
				log("tagged=" + !Tags_116.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()));*/
				if( RH.signValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
					
		            blockFace = BlockFace.EAST;
				}else if ( RH.signValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.signValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST;
		        }else if ( RH.signValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH;
		        }else {
		        	//log("north error");
		            return;
		        }
				//log("blockFace=" + blockFace);
				break;
			case EAST:
				if ( RH.signValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH; //log("south error");
		        }else if ( RH.signValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST; //log("west error");
		        }else if ( RH.signValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH; //log("north error");
		        }else if( RH.signValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST; //log("east error");
				}else {
		            return;
		        }
				break;
			case SOUTH:
				if ( RH.signValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
					
		            blockFace = BlockFace.WEST;
		        }else if ( RH.signValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH;
		        }else if( RH.signValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST;
				}else if ( RH.signValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else {
		            return;
		        }
				break;
			case WEST:
				if ( RH.signValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
					
		            blockFace=BlockFace.NORTH;
		        }else if( RH.signValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST;
				}else if ( RH.signValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.signValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST;
		        }else {
		            return;
		        }
				break;
			case UP:
				if ( RH.signValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
					
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.signValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		        	
		            blockFace = BlockFace.WEST;
		        }else if ( RH.signValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		        	
		            blockFace=BlockFace.NORTH;
		        }else if( RH.signValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		        	
		            blockFace = BlockFace.EAST;
				}else {
		            return;
		        }
				break;
			default:
				//log("error facing=" + facing);
				break;
	    	}
	    	//log("blockFace=" + blockFace);
	        face.setFacing(blockFace);
	    	state.setBlockData(face);
	    	state.update(true, true);
	    	world.getBlockAt(X-1, Y, Z).getState().update(true, true);
	    	world.getBlockAt(X+1, Y, Z).getState().update(true, true);
	    	world.getBlockAt(X, Y, Z-1).getState().update(true, true);
	    	world.getBlockAt(X, Y, Z+1).getState().update(true, true);
	    	world.getBlockAt(X, Y-1, Z).getState().update(true, true);
	    	world.getBlockAt(X, Y+1, Z).getState().update(true, true);
	    	if(world.getBlockAt(X+1, Y, Z).isEmpty()){
	    		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);//BARRIER
	    		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
	    	}else if(world.getBlockAt(X-1, Y, Z).isEmpty()){
	    		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
	    		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
	    	}else if(world.getBlockAt(X, Y, Z+1).isEmpty()){
	    		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
	    		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
	    	}else if(world.getBlockAt(X, Y, Z-1).isEmpty()){
	    		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
	    		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
	    	}else if(world.getBlockAt(X, Y+1, Z).isEmpty()){
	    		world.getBlockAt(X, Y+1, Z).setType(Material.BARRIER);
	    		world.getBlockAt(X, Y+1, Z).setType(Material.AIR);
	    	}else if(world.getBlockAt(X, Y-1, Z).isEmpty()){
	    		world.getBlockAt(X, Y-1, Z).setType(Material.BARRIER);
	    		world.getBlockAt(X, Y-1, Z).setType(Material.AIR);
	    	}
	    }
        
        /** Logs */ // TODO: Logs
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
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
        
        /** Stairs */ // TODO: Stairs
        if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
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
            state.update(true, true);
            World world = block.getWorld();
        	int X = block.getX();
        	int Y = block.getY();
        	int Z = block.getZ();
            if(world.getBlockAt(X+1, Y, Z).isEmpty()){
        		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X-1, Y, Z).isEmpty()){
        		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z+1).isEmpty()){
        		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z-1).isEmpty()){
        		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
        	}
        }else if( !isSpam && !event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		Tags.STAIRS.isTagged(block.getType()) && config.getBoolean("enabled.stairs.rotate", true) ){
        	BlockState state = block.getState();
        	Directional stair = (Directional) block.getBlockData();
        	Stairs stairs = (Stairs) state.getBlockData();
        	
        	BlockFace face = stair.getFacing();
        	BlockFace blockFace = null;
        	
        	World world = block.getWorld();
        	int X = block.getX();
        	int Y = block.getY();
        	int Z = block.getZ();
        	org.bukkit.block.data.type.Stairs.Shape shape = stairs.getShape();
        	switch(face){
        	case NORTH:
        		blockFace = BlockFace.EAST;
				break;
			case EAST:
				
				blockFace = BlockFace.SOUTH;
				break;
			case SOUTH:
				
				blockFace = BlockFace.WEST;
				break;
			case WEST:
				
				blockFace = BlockFace.NORTH;
				break;
			default:
				break;
        	
        	}
        	stair.setFacing(blockFace);
        	state.setBlockData(stair);
        	state.update(true, true);
        	if(world.getBlockAt(X+1, Y, Z).isEmpty()){
        		world.getBlockAt(X+1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X+1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X-1, Y, Z).isEmpty()){
        		world.getBlockAt(X-1, Y, Z).setType(Material.BARRIER);
        		world.getBlockAt(X-1, Y, Z).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z+1).isEmpty()){
        		world.getBlockAt(X, Y, Z+1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z+1).setType(Material.AIR);
        	}else if(world.getBlockAt(X, Y, Z-1).isEmpty()){
        		world.getBlockAt(X, Y, Z-1).setType(Material.BARRIER);
        		world.getBlockAt(X, Y, Z-1).setType(Material.AIR);
        	}
        	
        }
        
        /** Slabs */ // TODO: Slabs
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		Tags.SLABS.isTagged(block.getType()) && config.getBoolean("enabled.slabs", true) ){
        	BlockState state = block.getState();
        	Slab slab = (Slab) state.getBlockData();
			Type type = slab.getType();
			log("type=" + type);
			switch(type){
			case BOTTOM:
				slab.setType(Type.TOP);
				break;
			case TOP:
				slab.setType(Type.BOTTOM);
				break;
			case DOUBLE:
				break;
			}
			state.setBlockData(slab);
            state.update(true, true);
        }
        
        /** Beds */ // TODO: Beds
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
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
        /**if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().equals(wrench) && 
        		block.getType() == Material.CHAIN && config.getBoolean("enabled.chain", true) && v1_16_R2){
        	BlockState state = block.getState();
        	org.bukkit.block.data.type.Chain chain = (org.bukkit.block.data.type.Chain) state.getBlockData();
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
            state.update(true, true);
        }*/
        
        /** Grindstone */ // TODO: Grindstone
        if( !isSpam && event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		block.getType() == Material.GRINDSTONE && config.getBoolean("enabled.workstations", true)){
        	try{
	        	BlockState state = block.getState();
	        	Directional face = (Directional) state.getBlockData();
	        	BlockFace facing = face.getFacing();
	        	Face af = ((Switch) state.getBlockData()).getFace();
	        	
	        	//Face af = att.getFace();
	        	
	        	//FaceAttachable att = (FaceAttachable) state.getBlockData();
	        	//Face af = (Face) att.getAttachedFace();
	        	Location loc = block.getLocation();
	        	World world = loc.getWorld();
		        int X = loc.getBlockX();
		        int Y = loc.getBlockY();
		        int Z = loc.getBlockZ();
		        BlockFace blockFace = null;
		        Face Face = null;
		        
		        /**log("facing=" + facing.toString());
		        log("NORTH " + world.getBlockAt(X,Y,Z+1).getType().toString() + " empty=" + world.getBlockAt(X,Y,Z+1).isEmpty() + " solid=" + world.getBlockAt(X,Y,Z+1).getType().isSolid() + " tag=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z+1).getType()));
		        log("EAST " + world.getBlockAt(X-1,Y,Z).getType().toString() + " empty=" + world.getBlockAt(X-1,Y,Z).isEmpty() + " solid=" + world.getBlockAt(X-1,Y,Z).getType().isSolid() + " tag=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()));
		        log("SOUTH " + world.getBlockAt(X,Y,Z-1).getType().toString() + " empty=" + world.getBlockAt(X,Y,Z-1).isEmpty() + " solid=" + world.getBlockAt(X,Y,Z-1).getType().isSolid() + " tag=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y,Z-1).getType()));
		        log("WEST " + world.getBlockAt(X+1,Y,Z).getType().toString() + " empty=" + world.getBlockAt(X+1,Y,Z).isEmpty() + " solid=" + world.getBlockAt(X+1,Y,Z).getType().isSolid() + " tag=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X+1,Y,Z).getType()));
		        log("UP " + world.getBlockAt(X,Y+1,Z).getType().toString() + " empty=" + world.getBlockAt(X,Y+1,Z).isEmpty() + " solid=" + world.getBlockAt(X,Y+1,Z).getType().isSolid() + " tag=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y+1,Z).getType()));
		        log("DOWN " + world.getBlockAt(X,Y-1,Z).getType().toString() + " empty=" + world.getBlockAt(X,Y-1,Z).isEmpty() + " solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid() + " tag=" + Tags.NO_BUTTONS.isTagged(world.getBlockAt(X,Y-1,Z).getType()));//*/
		        switch(af){
				case FLOOR:
					Face = Face.WALL;
					break;
				case WALL:
					Face = Face.CEILING;
					break;
				case CEILING:
					Face = Face.FLOOR;
					break;
				default:
					break;
		        	
		        }
			      //log("blockFace=" + blockFace.toString());
					//block.setBlockData(face);
				//att.setAttachedFace(Face);
		        ((Switch) state).setFace(Face);
				//att.setFace(Face);
				//state.setBlockData(att);
					//state.update(true, true);
				state.update(true, true);
        	}catch(Exception e){}
        } //*/
        
        /** Trapdoors */ // TODO: Trapdoors
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		Tags.TRAPDOORS.isTagged(block.getType()) ){
        	event.setUseInteractedBlock(Result.DENY);
        	BlockState state = block.getState();
    		TrapDoor trapdoor = (TrapDoor) state.getBlockData();
    		BlockFace facing = trapdoor.getFacing();
    		Half half = trapdoor.getHalf();
    		if(event.getPlayer().isSneaking() && config.getBoolean("enabled.trapdoors.invert", true) ){
	    		/** half */
	    		switch(half){
	    		case TOP:
	    			trapdoor.setHalf(Half.BOTTOM);
	    			break;
	    		case BOTTOM:
	    			trapdoor.setHalf(Half.TOP);
	    			break;
	    		}
	    		state.setBlockData(trapdoor);
	            state.update(true, true);
	            event.setUseInteractedBlock(Result.DENY);
    		}else if(!event.getPlayer().isSneaking() && config.getBoolean("enabled.trapdoors.rotate", true) ){
    			//log("true");
	    		/** facing */
	    		switch(facing){
	    		case NORTH:
	    			trapdoor.setFacing(BlockFace.EAST);
	    			break;
	    		case EAST:
	    			trapdoor.setFacing(BlockFace.SOUTH);
	    			break;
	    		case SOUTH:
	    			trapdoor.setFacing(BlockFace.WEST);
	    			break;
	    		case WEST:
	    			trapdoor.setFacing(BlockFace.NORTH);
	    			break;
				default:
					//log("default");
					break;
	    		}
	    		state.setBlockData(trapdoor);
	            state.update(true, true);
	            event.setUseInteractedBlock(Result.DENY);
	    		//log("facing=" + facing);
    		}
        }
    	
        /** Rails */
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist && 
        		Tags.RAILS.isTagged(block.getType()) && config.getBoolean("enabled.rails", true) ){
        	BlockState state = block.getState();
        	Rail rail = (Rail) block.getBlockData();
			Shape shape = rail.getShape();
			//log("shape=" + shape.toString());
			if(block.getType().equals(Material.RAIL)){
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
			}else{
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
				case NORTH_SOUTH:
					rail.setShape(Shape.EAST_WEST);
					break;
				case EAST_WEST:
					rail.setShape(Shape.NORTH_SOUTH);
					break;
				default:
					break;
        		}
        	}
			state.setBlockData(rail);
            state.update(true, true);

			//rail.setShape(paramShape);
        }
        
        /** Coral */ // TODO: Coral
        if( !isSpam && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && 
        		event.getItem().equals(wrench) && !onblacklist &&  
        		( Tags.CORAL.isTagged(block.getType()) && RW.getBoolean("enabled.coral", true) ||
        				Tags.CORAL_WALL.isTagged(block.getType()) && RW.getBoolean("enabled.coral", true) ) ){//config.getBoolean("enabled.coral", true) ){
        	
        	coral_fan.onBlockClick(event);
        	
        	
        }
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
	}
	
	@EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent event) {
		Entity entity = event.getRightClicked();
		boolean onblacklist = false;
        onblacklist = StrUtils.stringContains(config.getString("blacklist", ""),event.getPlayer().getWorld().getName());
        /** Armor Stands */
        if( !event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().equals(wrench)  
        		&& entity instanceof ArmorStand && config.getBoolean("enabled.armorstands", true) && !onblacklist ){
        	ArmorStand as = (ArmorStand) entity;
        	float yaw = as.getLocation().getYaw();
        	yaw = yaw + 45;
        	Location loc = as.getLocation();
        	loc.setYaw(yaw);
			as.teleport(loc);
			//log("yaw=" + yaw);

        }if( event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().equals(wrench)  
        		&& entity instanceof ArmorStand && config.getBoolean("enabled.armorstands", true) && !onblacklist ){
        	ArmorStand as = (ArmorStand) entity;
        	float yaw = as.getLocation().getYaw();
        	yaw = yaw + 1;
        	Location loc = as.getLocation();
        	loc.setYaw(yaw);
			as.teleport(loc);
			//log("yaw=" + yaw);
        }
	}
	
	public	void log(String dalog){// TODO: log
		PluginDescriptionFile pdfFile = plugin.getDescription();
		logger.info(ChatColor.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + ChatColor.RESET + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + dalog);
	}
	public void logWarn(String dalog){
		log(ChatColor.RED + "[WARN] " + ChatColor.RESET  + dalog);
	}
}
