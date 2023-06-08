package com.github.joelgodofwar.rw.util;

import java.util.logging.Logger;

import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Snow;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.Wall.Height;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.github.joelgodofwar.rw.RotationalWrench;

public class RotateHelper {
	public final static Logger logger = Logger.getLogger("Minecraft");
	public final RotationalWrench RW;
	public static boolean debug = false;
	
	public RotateHelper(final RotationalWrench plugin){
		RW = plugin;
		debug = RW.isDebug();
	}
	//RotationalWrench RW = new RotationalWrench();
	public static BlockFace getLeftDirection(BlockFace forward) {
		switch (forward) {
		case EAST:
			return BlockFace.NORTH;
		case NORTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.SOUTH;
		case SOUTH:
			return BlockFace.EAST;
		default:
			return forward;
		}
	}
	
	public static BlockFace getRightDirection(BlockFace forward) {
		switch (forward) {
		case EAST:
			return BlockFace.SOUTH;
		case SOUTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.NORTH;
		case NORTH:
			return BlockFace.EAST;
		default:
			return forward;
		}
	}
	
    public static BlockFace getClosestFace(Block b, Player p) {
		float direction = (float) Math.toDegrees(Math.atan2(p.getLocation().getBlockX() - b.getX(), b.getZ() - p.getLocation().getBlockZ()));
        direction = direction % 360;

        if (direction < 0)
            direction += 360;

        direction = Math.round(direction / 90);

        switch ((int) direction) {
        case 0:
            return BlockFace.WEST;
        case 1:
            return BlockFace.NORTH;
        case 2:
            return BlockFace.EAST;
        case 3:
            return BlockFace.SOUTH;
        default:
            return BlockFace.WEST;
        }
    }
	
    public static Height nextHeight(Height height, boolean sneak) {
    	if(sneak) {
    		switch (height) {
			case LOW:
				return Height.NONE;
			case NONE:
				return Height.TALL;
			case TALL:
				return Height.LOW;
			default:
				return Height.NONE;
	    	}
    	}else {
	    	switch (height) {
			case LOW:
				return Height.TALL;
			case NONE:
				return Height.LOW;
			case TALL:
				return Height.NONE;
			default:
				return Height.NONE;
	    	}
    	}
    }
    
	public boolean coralValidBlock(World world, int X, int Y, int Z, BlockFace blockface){
		Block block = world.getBlockAt(X, Y, Z);
		if( !block.isEmpty() == false)									{	return false;	}
		if( !Tags_116.CORAL.isTagged(block.getType()) == false)			{	return false;	}
		if( !Tags_116.CORAL_WALL.isTagged(block.getType()) == false)	{	return false;	}
		if( block.getType().isSolid() == false)							{	return false;	}
		if(block.getType().toString().contains("PATH")) 				{	return false;	}
		if( !Tags_116.NO_SIGNS_TOP.isTagged(block.getType()) == false)	{	return true;	}
		if( block.getType().toString().contains("_STAIRS") )					{	return true;	}
		if( SameFacing(world, X, Y, Z, blockface)  == false)			{	return false;	}
    	return true;
    }
	
	public boolean coralValidBlock2(World world, int X, int Y, int Z, Half blockface){
		Block block = world.getBlockAt(X, Y, Z);
		if( !block.isEmpty() == false){			return false;		}
		if( !Tags_116.CORAL.isTagged(block.getType()) == false){			return false;		}
		if( !Tags_116.CORAL_WALL.isTagged(block.getType()) == false){			return false;		}
		if( block.getType().isSolid() == false){			return false;		}
		if( !Tags_116.NO_SIGNS_SIDE.isTagged(block.getType()) == false){			return true;		}
		if( block.getType().toString().contains("_STAIRS") ){			return true;		}
		if( SameHalf(world, X, Y, Z, blockface)  == false){			return false;		}
    	return true;
    }
	
	public boolean signValidBlock(World world, int X, int Y, int Z, BlockFace blockface){
		Block block = world.getBlockAt(X, Y, Z);
		if( !block.isEmpty() == false){			if(debug){logDebug("sVB Empty");}return false;		}
		/**if( Tags_116.YES_SIGNS.isTagged(block.getType()) || Tags_116.SIGNS.isTagged(block.getType()) || Tags_116.SIGNS2.isTagged(block.getType()) ){
			return true;
		}//*/
		
		if( block.getType().isSolid() == false)							{			if(debug){logDebug(ChatColor.GREEN + "sVB Solid");}return false;		}
		if( block.getType().toString().contains("PATH") ) 				{	return false;	}
		if( !Tags_116.NO_SIGNS_TOP.isTagged(block.getType()) == false)	{			if(debug){logDebug("sVB !SignTop");}return true;		}
		if( block.getType().toString().contains("_STAIRS") )					{			return true;		}
		if( Tags_116.TRAPDOORS.isTagged(block.getType()) )				{			return true;		}
		if( SameFacing(world, X, Y, Z, blockface)  == false)			{			if(debug){logDebug("sVB Facing");}return false;		}
    	return true;
    }
	
	public boolean signValidBlock2(World world, int X, int Y, int Z, Half blockface){
		Block block = world.getBlockAt(X, Y, Z);
		if( !block.isEmpty() == false){			if(debug){logDebug("sVB2 Empty");}return false;		}
		/**if( Tags_116.YES_SIGNS.isTagged(block.getType()) || Tags_116.SIGNS.isTagged(block.getType()) || Tags_116.SIGNS2.isTagged(block.getType()) ){
			return true;
		}//*/
		if( block.getType().isSolid() == false){			if(debug){logDebug("sVB2 Solid");}return false;		}
		if( !Tags_116.NO_SIGNS_SIDE.isTagged(block.getType()) == false){			return true;		}
		if( block.getType().toString().contains("_STAIRS") ){			return true;		}
		if( Tags_116.TRAPDOORS.isTagged(block.getType()) ){			return true;		}
		if( SameHalf(world, X, Y, Z, blockface)  == false){			if(debug){logDebug("sVB2 Half");}return false;		}
    	return true;
    }
	
	public boolean lanternValidBlock(World world, int X, int Y, int Z, Half blockface){
		Block block = world.getBlockAt(X, Y, Z);
		if( !world.getBlockAt(X, Y, Z).isEmpty() == false){			return false;		}
		if( world.getBlockAt(X, Y, Z).getType().isSolid() == false){			return false;		}
		if( !Tags_116.NO_LANTERNS.isTagged(world.getBlockAt(X, Y, Z).getType()) == false){			return false;		}
		if( !Tags_116.SIGNS.isTagged(world.getBlockAt(X, Y, Z).getType()) == false){			return false;		}
		if( !Tags_116.SIGNS2.isTagged(world.getBlockAt(X, Y, Z).getType()) == false){			return false;		}
		if( Tags_116.TRAPDOORS.isTagged(block.getType()) ){			//TRAPDOORS
			BlockState state = block.getState();
	    	TrapDoor stairs = (TrapDoor) state.getBlockData();
	    	if(stairs.isOpen()){
	    		if(debug){logDebug("tVB2 TD Open");}return false;
	    	}else{return true;}
		}
		if( SameHalf(world, X, Y, Z, blockface)  == false){			return false;		}
    	return true;
    }
	
	public boolean buttonValidBlock(World world, int X, int Y, int Z, Half blockface){
		Block block = world.getBlockAt(X, Y, Z);
		if( !world.getBlockAt(X, Y, Z).isEmpty() == false){
			if(debug){logDebug("bVB Empty");}return false;		}
			if(debug){logDebug("bVB Mat " + block.getType().toString());}
		if(block.getType().equals(Material.SNOW)){
			Snow snow = (Snow) block.getBlockData();
			int layers = snow.getLayers();
			if(layers == 8){
				return true;
			}
		}
		if(block.getType().equals(Material.CHORUS_FLOWER)){
			return true;
		}
		if(block.getType().equals(Material.SCAFFOLDING)){
			return true;
		}
		if( world.getBlockAt(X, Y, Z).getType().isSolid() == false){
			if(debug){logDebug("bVB Solid");}return false;		}
		if( !Tags_116.NO_BUTTONS.isTagged(world.getBlockAt(X, Y, Z).getType()) == false){
			if(debug){logDebug("bVB Buttons");}return false;		}
		if( ( block.getType().equals(Material.FARMLAND) || block.getType().toString().contains("PATH") ) && blockface.equals(Half.BOTTOM) ){
			return true;
		}
		if( !Tags_116.SIGNS.isTagged(world.getBlockAt(X, Y, Z).getType()) == false){
			if(debug){logDebug("bVB Signs");}return false;		}
		if( !Tags_116.SIGNS2.isTagged(world.getBlockAt(X, Y, Z).getType()) == false){
			if(debug){logDebug("bVB Signs2");}return false;		}
		if( SameHalf(world, X, Y, Z, blockface)  == false){
			if(debug){logDebug("bVB Half");}return false;		}
    	return true;
    }
	
	public boolean pistonValidBlock(World world, int X, int Y, int Z, boolean extended){
		if(extended){
			if( world.getBlockAt(X, Y, Z).isEmpty() == false){			return false;		}
		}
		return true;
    }
	
	public boolean torchValidBlock(World world, int X, int Y, int Z, BlockFace blockface){
		Block block = world.getBlockAt(X, Y, Z);
		
		if( !block.isEmpty() == false){
			if(debug){logDebug("tVB Empty " + block.getType().toString());}return false;		}

			//log("tVB Mat " + block.getType().toString() + " solid " + block.getType().isSolid());
		if(block.getType().equals(Material.SNOW)){
				Snow snow = (Snow) block.getBlockData();
				int layers = snow.getLayers();
				if(layers == 8){					return true;				}
		}
		
		if(block.getType().equals(Material.CHORUS_FLOWER)){
			return true;
		}

		if( block.getType().isSolid() == false){
			if(debug){logDebug("tVB Solid " + block.getType().toString());}return false;		}

		if( !Tags_116.NO_BUTTONS.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB Buttons " + block.getType().toString());}return false;		}

		if( !Tags_116.SIGNS.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB Signs " + block.getType().toString());}return false;		}

		if( !Tags_116.SIGNS2.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB Signs2 " + block.getType().toString());}return false;		}
		if( block.getType().toString().contains("PATH") ) {	return false;	}
		if( !Tags_116.NO_TORCH_SIDE.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB !Torch Side " + block.getType().toString());}return false;		}

		if( SameFacing(world, X, Y, Z, blockface)  == false){
			if(debug){logDebug("tVB Facing " + block.getType().toString());}return false;		}

		if( block.getType().toString().contains("_SLAB") ){
			Slab slab = (Slab) block.getState().getBlockData();
			if(!slab.getType().equals(Slab.Type.DOUBLE)){
				if(debug){logDebug("tVB Slabs " + block.getType().toString());}return false;
			}
		}
    	return true;
    }
	
	public boolean torchValidBlock2(World world, int X, int Y, int Z, Half blockface){ //TODO: TorchValidBlock2
		Block block = world.getBlockAt(X, Y, Z);
		if( !block.isEmpty() == false){
			if(debug){logDebug("tVB2 Empty");}return false;		}
		if(block.getType().equals(Material.SNOW)){
			Snow snow = (Snow) block.getBlockData();
			int layers = snow.getLayers();
			if(layers == 8){
				return true;
			}
		}
		if(block.getType().equals(Material.CHORUS_FLOWER)){
			return true;
		}
		if( block.getType().equals(Material.CHAIN) ){			BlockState state = block.getState();
		Orientable chain = (Orientable) state.getBlockData();        	Axis axis = chain.getAxis();
        	if(axis.equals(Axis.Y)){return true;}else{log("tVB2 Chain!Y");return false;}		}
		if( Tags_116.TRAPDOORS.isTagged(block.getType()) ){			//TRAPDOORS
			BlockState state = block.getState();
	    	TrapDoor stairs = (TrapDoor) state.getBlockData();
	    	if(stairs.isOpen()){
	    		if(debug){logDebug("tVB2 TD Open");}return false;
	    	}else{return true;}
		}
		if(block.getType().equals(Material.END_ROD)){
			Directional state = (Directional) block.getBlockData();
			switch(state.getFacing()){
			case UP:
			case DOWN:
				return true;
			default:
				if(debug){logDebug("tVB2 ER !^v");}return false;
			}
		}
		if(block.getType().equals(Material.SCAFFOLDING)){
			return true;
		}
		if( block.getType().isSolid() == false){
			if(debug){logDebug("tVB2 !Solid");}return false;		}
		if( !Tags_116.BUTTONS.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB2 !Buttons");}return false;		}
		if( !Tags_116.SIGNS.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB2 !Signs");}return false;		}
		if( !Tags_116.SIGNS2.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB2 !Signs2");}return false;		}
		if( block.getType().toString().contains("PATH") ) {	return false; }
		if( !Tags_116.NO_TORCH_TOP.isTagged(block.getType()) == false){
			if(debug){logDebug("tVB2 !Torch Top");}return false;		}
		if( SameHalf(world, X, Y, Z, blockface)  == false){
			if(debug){logDebug("tVB2 !Half");}return false;		}
		if( block.getType().toString().contains("_SLAB") ){
			Slab slab = (Slab) block.getState().getBlockData();
			if(!slab.getType().equals(Slab.Type.DOUBLE) && !slab.getType().equals(Slab.Type.TOP) ){
				if(debug){logDebug("tVB2 !Slabs");}return false;		}}
    	return true;
    }
	
	public boolean SameFacing(World world, int X, int Y, int Z, BlockFace blockface){
		if( world.getBlockAt(X,Y,Z).getType().toString().contains("_STAIRS") && 
				!((Directional) world.getBlockAt(X,Y,Z).getState().getBlockData()).getFacing().equals(blockface)){
			return false;
		}else if( Tags_116.TRAPDOORS.isTagged(world.getBlockAt(X,Y,Z).getType()) ){
			Block block = world.getBlockAt(X,Y,Z);
			BlockState state = block.getState();
	    	TrapDoor stairs = (TrapDoor) state.getBlockData();
	    	if(!stairs.isOpen()){
	    		return false;
	    	}
	    	BlockFace half = stairs.getFacing().getOppositeFace();
			if(!half.equals(blockface)){
				return false;
			}
		}
		return true;
	}
	
	public boolean SameHalf(World world, int X, int Y, int Z, Half blockface){
		Type blockface2 = null;
		//half.equals(Half.BOTTOM)
		if( world.getBlockAt(X,Y,Z).getType().toString().contains("_STAIRS") ){
			Block block = world.getBlockAt(X,Y,Z);
			BlockState state = block.getState();
	    	Stairs stairs = (Stairs) state.getBlockData();
			Half half = stairs.getHalf();
			if(!half.equals(blockface)){
				return false;
			}
		}else if( world.getBlockAt(X,Y,Z).getType().toString().contains("_SLAB") ){
			// block.getType().toString().contains("_SLAB")
			//Tags_116.SLABS.isTagged(world.getBlockAt(X,Y,Z).getType())
			if(blockface.equals(Bisected.Half.BOTTOM)){
				blockface2 = Slab.Type.BOTTOM;
			}else if(blockface.equals(Bisected.Half.TOP)){
				blockface2 = Slab.Type.TOP;
			}
			Block block = world.getBlockAt(X,Y,Z);
			BlockState state = block.getState();
			Slab stairs = (Slab) state.getBlockData();
			Type half = stairs.getType();
			if(!half.equals(blockface2)&&!half.equals(Type.DOUBLE)){
				//log("slab false");
				return false;
			}
		}else if( Tags_116.TRAPDOORS.isTagged(world.getBlockAt(X,Y,Z).getType()) ){
			Block block = world.getBlockAt(X,Y,Z);
			BlockState state = block.getState();
	    	TrapDoor stairs = (TrapDoor) state.getBlockData();
			Half half = stairs.getHalf();
			if(!half.equals(blockface)){
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("incomplete-switch")
	public  Material getOpposite(Material material){
		switch(material){
		case OAK_SIGN:
			return Material.OAK_WALL_SIGN;
		case SPRUCE_SIGN:
			return Material.SPRUCE_WALL_SIGN;
		case BIRCH_SIGN:
			return Material.BIRCH_WALL_SIGN;
		case JUNGLE_SIGN:
			return Material.JUNGLE_WALL_SIGN;
		case ACACIA_SIGN:
			return Material.ACACIA_WALL_SIGN;
		case DARK_OAK_SIGN:
			return Material.DARK_OAK_WALL_SIGN;
		case CRIMSON_SIGN:
			return Material.CRIMSON_WALL_SIGN;
		case WARPED_SIGN:
			return Material.WARPED_WALL_SIGN;
			
		case OAK_WALL_SIGN:
			return Material.OAK_SIGN;
		case SPRUCE_WALL_SIGN:
			return Material.SPRUCE_SIGN;
		case BIRCH_WALL_SIGN:
			return Material.BIRCH_SIGN;
		case JUNGLE_WALL_SIGN:
			return Material.JUNGLE_SIGN;
		case ACACIA_WALL_SIGN:
			return Material.ACACIA_SIGN;
		case DARK_OAK_WALL_SIGN:
			return Material.DARK_OAK_SIGN;
		case CRIMSON_WALL_SIGN:
			return Material.CRIMSON_SIGN;
		case WARPED_WALL_SIGN:
			return Material.WARPED_SIGN;
			
		case WHITE_BANNER:
			return Material.WHITE_WALL_BANNER;
		case ORANGE_BANNER:
			return Material.ORANGE_WALL_BANNER;
		case MAGENTA_BANNER:
			return Material.MAGENTA_WALL_BANNER;
		case LIGHT_BLUE_BANNER:
			return Material.LIGHT_BLUE_WALL_BANNER;
		case YELLOW_BANNER:
			return Material.YELLOW_WALL_BANNER;
		case LIME_BANNER:
			return Material.LIME_WALL_BANNER;
		case PINK_BANNER:
			return Material.PINK_WALL_BANNER;
		case GRAY_BANNER:
			return Material.GRAY_WALL_BANNER;
		case LIGHT_GRAY_BANNER:
			return Material.LIGHT_GRAY_WALL_BANNER;
		case CYAN_BANNER:
			return Material.CYAN_WALL_BANNER;
		case PURPLE_BANNER:
			return Material.PURPLE_WALL_BANNER;
		case BLUE_BANNER:
			return Material.BLUE_WALL_BANNER;
		case BROWN_BANNER:
			return Material.BROWN_WALL_BANNER;
		case GREEN_BANNER:
			return Material.GREEN_WALL_BANNER;
		case RED_BANNER:
			return Material.RED_WALL_BANNER;
		case BLACK_BANNER:
			return Material.BLACK_WALL_BANNER;
			
		case WHITE_WALL_BANNER:
			return Material.WHITE_BANNER;
		case ORANGE_WALL_BANNER:
			return Material.ORANGE_BANNER;
		case MAGENTA_WALL_BANNER:
			return Material.MAGENTA_BANNER;
		case LIGHT_BLUE_WALL_BANNER:
			return Material.LIGHT_BLUE_BANNER;
		case YELLOW_WALL_BANNER:
			return Material.YELLOW_BANNER;
		case LIME_WALL_BANNER:
			return Material.LIME_BANNER;
		case PINK_WALL_BANNER:
			return Material.PINK_BANNER;
		case GRAY_WALL_BANNER:
			return Material.GRAY_BANNER;
		case LIGHT_GRAY_WALL_BANNER:
			return Material.LIGHT_GRAY_BANNER;
		case CYAN_WALL_BANNER:
			return Material.CYAN_BANNER;
		case PURPLE_WALL_BANNER:
			return Material.PURPLE_BANNER;
		case BLUE_WALL_BANNER:
			return Material.BLUE_BANNER;
		case BROWN_WALL_BANNER:
			return Material.BROWN_BANNER;
		case GREEN_WALL_BANNER:
			return Material.GREEN_BANNER;
		case RED_WALL_BANNER:
			return Material.RED_BANNER;
		case BLACK_WALL_BANNER:
			return Material.BLACK_BANNER;
			
		case DEAD_BRAIN_CORAL_FAN:
			return Material.DEAD_BRAIN_CORAL_WALL_FAN;
		case DEAD_BUBBLE_CORAL_FAN:
			return Material.DEAD_BUBBLE_CORAL_WALL_FAN;
		case DEAD_FIRE_CORAL_FAN:
			return Material.DEAD_FIRE_CORAL_WALL_FAN;
		case DEAD_HORN_CORAL_FAN:
			return Material.DEAD_HORN_CORAL_WALL_FAN;
		case DEAD_TUBE_CORAL_FAN:
			return Material.DEAD_TUBE_CORAL_WALL_FAN;
		case BRAIN_CORAL_FAN:
			return Material.BRAIN_CORAL_WALL_FAN;
		case BUBBLE_CORAL_FAN:
			return Material.BUBBLE_CORAL_WALL_FAN;
		case FIRE_CORAL_FAN:
			return Material.FIRE_CORAL_WALL_FAN;
		case HORN_CORAL_FAN:
			return Material.HORN_CORAL_WALL_FAN;
		case TUBE_CORAL_FAN:
			return Material.TUBE_CORAL_WALL_FAN;
		
		case DEAD_BRAIN_CORAL_WALL_FAN:
			return Material.DEAD_BRAIN_CORAL_FAN;
		case DEAD_BUBBLE_CORAL_WALL_FAN:
			return Material.DEAD_BUBBLE_CORAL_FAN;
		case DEAD_FIRE_CORAL_WALL_FAN:
			return Material.DEAD_FIRE_CORAL_FAN;
		case DEAD_HORN_CORAL_WALL_FAN:
			return Material.DEAD_HORN_CORAL_FAN;
		case DEAD_TUBE_CORAL_WALL_FAN:
			return Material.DEAD_TUBE_CORAL_FAN;
		case BRAIN_CORAL_WALL_FAN:
			return Material.BRAIN_CORAL_FAN;
		case BUBBLE_CORAL_WALL_FAN:
			return Material.BUBBLE_CORAL_FAN;
		case FIRE_CORAL_WALL_FAN:
			return Material.FIRE_CORAL_FAN;
		case HORN_CORAL_WALL_FAN:
			return Material.HORN_CORAL_FAN;
		case TUBE_CORAL_WALL_FAN:
			return Material.TUBE_CORAL_FAN;
            

		}
		return null;
		
	}
	
	public	void log(String dalog){// TODO: log
		PluginDescriptionFile pdfFile = RW.getDescription();
		logger.info(ChatColor.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + ChatColor.RESET + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + dalog);
	}
	public void logWarn(String dalog){
		log(ChatColor.RED + "[WARN] " + ChatColor.RESET  + dalog);
	}
	
	public void blockGrid2(World world, int X, int Y, int Z){
		Location loc = new Location(world, X, Y, Z);
		loc.add(0, 1, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 1, 0);
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
	}
	
	public void blockGrid(World world, int X, int Y, int Z){
		Location loc = new Location(world, X, Y, Z);
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc = new Location(world, X, Y, Z);
		loc.subtract(0, 1, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc = new Location(world, X, Y, Z);
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.subtract(0, 0, 1);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
		loc.add(1, 0, 0);
		if(world.getBlockAt(loc).isEmpty()){
    		world.getBlockAt(loc).setType(Material.BARRIER);
    		world.getBlockAt(loc).setType(Material.AIR);
    	}
        //world.getBlockAt(loc).getState().update(true, true);
	}
}
