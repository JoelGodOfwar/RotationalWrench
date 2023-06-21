package com.github.joelgodofwar.rw.event.one14;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.util.RotateHelper;
import com.github.joelgodofwar.rw.util.Tags_116;

public class Coral_Fan {
	
	public RotateHelper RH;
	public final RotationalWrench RW;
	
	public Coral_Fan(final RotationalWrench plugin){
		RW = plugin;
		RH = new RotateHelper(RW);
	}
	@EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
		
		Block block = event.getClickedBlock();
		BlockState state = block.getState();
    	World world = block.getWorld();
    	int X = block.getX();
    	int Y = block.getY();
    	int Z = block.getZ();
    	BlockFace blockFace = null;
    	
    	if(event.getPlayer().isSneaking()){
	    	if( Tags_116.CORAL.isTagged(block.getType()) ){
	    		if( RH.coralValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
		            blockFace = BlockFace.EAST;
				}else if ( RH.coralValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
		            blockFace=BlockFace.SOUTH;
		        }else if ( RH.coralValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
		            blockFace = BlockFace.WEST;
		        }else if ( RH.coralValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
		            blockFace=BlockFace.NORTH;
		        }else{
		        	return;
		        }
	    		BlockData mat = Material.getMaterial(RH.getOpposite(block.getType()).toString()).createBlockData();
	    		boolean iwl = ((Waterlogged) block.getBlockData()).isWaterlogged();
	    		((Waterlogged) mat).setWaterlogged(iwl);
	    		((Directional) mat).setFacing(blockFace);
	    		
	    		state.setBlockData(mat);
	    		state.update(true, true);
	    	}else if( Tags_116.CORAL_WALL.isTagged(block.getType()) ){
	    		if( RH.coralValidBlock2(world, X, Y-1, Z, Half.TOP) ){ // North
	    			BlockData mat = Material.getMaterial(RH.getOpposite(block.getType()).toString()).createBlockData();
	    			boolean iwl = ((Waterlogged) block.getBlockData()).isWaterlogged();
	        		((Waterlogged) mat).setWaterlogged(iwl);
	        		state.setBlockData(mat);
	        		state.update(true, true);
				}
	    	}
    	}else if(!event.getPlayer().isSneaking()){
    		Directional face = (Directional) state.getBlockData();
        	BlockFace facing = face.getFacing();
        	switch(facing){
    		case NORTH:
    			/**log("empty=" + !world.getBlockAt(X,Y-1,Z).isEmpty());
    			log("solid=" + world.getBlockAt(X,Y-1,Z).getType().isSolid());
    			log("tagged=" + !Tags_116.NO_BUTTONS.isTagged(world.getBlockAt(X-1,Y,Z).getType()));*/
    			if( RH.coralValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
    				
    	            blockFace = BlockFace.EAST;
    			}else if ( RH.coralValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
    				
    	            blockFace=BlockFace.SOUTH;
    	        }else if ( RH.coralValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
    	        	
    	            blockFace = BlockFace.WEST;
    	        }else if ( RH.coralValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
    	        	
    	            blockFace=BlockFace.NORTH;
    	        }else {
    	        	//log("north error");
    	            return;
    	        }
    			//log("blockFace=" + blockFace);
    			break;
    		case EAST:
    			if ( RH.coralValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
    				
    	            blockFace=BlockFace.SOUTH; //log("south error");
    	        }else if ( RH.coralValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
    	        	
    	            blockFace = BlockFace.WEST; //log("west error");
    	        }else if ( RH.coralValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
    	        	
    	            blockFace=BlockFace.NORTH; //log("north error");
    	        }else if( RH.coralValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
    	        	
    	            blockFace = BlockFace.EAST; //log("east error");
    			}else {
    	            return;
    	        }
    			break;
    		case SOUTH:
    			if ( RH.coralValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
    				
    	            blockFace = BlockFace.WEST;
    	        }else if ( RH.coralValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
    	        	
    	            blockFace=BlockFace.NORTH;
    	        }else if( RH.coralValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
    	        	
    	            blockFace = BlockFace.EAST;
    			}else if ( RH.coralValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
    				
    	            blockFace=BlockFace.SOUTH;
    	        }else {
    	            return;
    	        }
    			break;
    		case WEST:
    			if ( RH.coralValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
    				
    	            blockFace=BlockFace.NORTH;
    	        }else if( RH.coralValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
    	        	
    	            blockFace = BlockFace.EAST;
    			}else if ( RH.coralValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
    				
    	            blockFace=BlockFace.SOUTH;
    	        }else if ( RH.coralValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
    	        	
    	            blockFace = BlockFace.WEST;
    	        }else {
    	            return;
    	        }
    			break;
    		case UP:
    			if ( RH.coralValidBlock(world, X, Y, Z-1, BlockFace.SOUTH) ){ // East
    				
    	            blockFace=BlockFace.SOUTH;
    	        }else if ( RH.coralValidBlock(world, X+1, Y, Z, BlockFace.WEST) ){ // South
    	        	
    	            blockFace = BlockFace.WEST;
    	        }else if ( RH.coralValidBlock(world, X, Y, Z+1, BlockFace.NORTH) ){ // West
    	        	
    	            blockFace=BlockFace.NORTH;
    	        }else if( RH.coralValidBlock(world, X-1, Y, Z, BlockFace.EAST) ){ // North
    	        	
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
    	
	}
}
