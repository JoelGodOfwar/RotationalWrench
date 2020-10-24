package com.github.joelgodofwar.rw.nms;

import java.util.List;
import java.util.Objects;
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
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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
import com.github.joelgodofwar.rw.util.Ansi;
import com.github.joelgodofwar.rw.util.RotateHelper;
import com.github.joelgodofwar.rw.util.Tags;
import com.github.joelgodofwar.rw.util.YmlConfiguration;
import com.google.common.collect.Lists;

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
	boolean colorful_console = true;
	boolean UpdateAvailable =	false;
	public final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
	boolean v1_14_R = false;
	
	public RW_1_14_R1(RotationalWrench plugin){
		this.config = plugin.config;
		//wrench = plugin.wrench;
		this.plugin = plugin;
		ItemMeta meta = Objects.requireNonNull(wrench.getItemMeta());
        meta.setDisplayName(ChatColor.RESET + "Rotational Wrench");
        meta.setUnbreakable(true);
        meta.setCustomModelData(4321);
        wrench.setItemMeta(meta);
        String packageName = plugin.getServer().getClass().getPackage().getName();
    	String version = packageName.substring(packageName.lastIndexOf('.') + 2);
    	if( version.contains("1_14_R") || version.contains("1_15_R") ){
    		v1_14_R = true;
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
        				Tags.END_ROD.isTagged(block.getType()) && config.getBoolean("enabled.endrod", true) ||
        				v1_14_R && Tags.BELL.isTagged(block.getType()) && config.getBoolean("enabled.bell", true) ) ) {
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
        		( Tags.SLABS.isTagged(block.getType()) || Tags.SLABS_114.isTagged(block.getType())  ) && config.getBoolean("enabled.slabs", true) ){
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
            state.update(false, false);
        }*/
        
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
	
	public	void log(String dalog){// TODO: log
		PluginDescriptionFile pdfFile = plugin.getDescription();
		logger.info(Ansi.AnsiColor("YELLOW", colorful_console) + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.AnsiColor("RESET", colorful_console) + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(Ansi.AnsiColor("RED", colorful_console) + "[DEBUG] " + Ansi.AnsiColor("RESET", colorful_console) + dalog);
	}
	public void logWarn(String dalog){
		log(Ansi.AnsiColor("RED", colorful_console) + "[WARN] " + Ansi.AnsiColor("RESET", colorful_console)  + dalog);
	}
}
