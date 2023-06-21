package com.github.joelgodofwar.rw.util;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {
	
	public static void sendJson(CommandSender player, String string){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
	}
	public static void sendJson(Player player, String string){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
	}
	public static int getOrdinal(BlockFace blockFace) {
	    switch (blockFace) {
	        case SOUTH: 			return 0;
	        case SOUTH_SOUTH_WEST: 	return 1;
	        case SOUTH_WEST: 		return 2;
	        case WEST_SOUTH_WEST: 	return 3;
	        case WEST: 				return 4;
	        case WEST_NORTH_WEST: 	return 5;
	        case NORTH_WEST: 		return 6;
	        case NORTH_NORTH_WEST: 	return 7;
	        case NORTH: 			return 8;
	        case NORTH_NORTH_EAST: 	return 9;
	        case NORTH_EAST: 		return 10;
	        case EAST_NORTH_EAST: 	return 11;
	        case EAST: 				return 12;
	        case EAST_SOUTH_EAST: 	return 13;
	        case SOUTH_EAST: 		return 14;
	        case SOUTH_SOUTH_EAST: 	return 15;
	        default: return -1;
	    }
	}

}
