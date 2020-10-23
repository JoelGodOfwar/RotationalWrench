package com.github.joelgodofwar.rw.util;

import org.bukkit.block.BlockFace;

public class RotateHelper {
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
}
