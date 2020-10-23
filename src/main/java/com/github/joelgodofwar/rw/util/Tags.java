package com.github.joelgodofwar.rw.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

@SuppressWarnings("deprecation")
public class Tags {
	public static final Tag<Material> REDSTONE_COMPONENTS = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.REPEATER,		Material.COMPARATOR,            Material.OBSERVER,
            Material.DISPENSER,     Material.DROPPER,	            Material.HOPPER,
            Material.STICKY_PISTON, Material.PISTON,	            Material.FURNACE,
            Material.ANVIL
    );
	
	public static final Tag<Material> CARVED_PUMPKIN = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.CARVED_PUMPKIN
    );
	
	public static final Tag<Material> END_ROD = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.END_ROD
    );
	
	public static final Tag<Material> BELL = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.BELL
    );
	
	public static final Tag<Material> LOGS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_LOG, 		Material.ACACIA_LOG, 	Material.BIRCH_LOG,
			Material.CRIMSON_STEM,	Material.DARK_OAK_LOG,	Material.JUNGLE_LOG,
			Material.SPRUCE_LOG, 	Material.WARPED_STEM,
			Material.STRIPPED_OAK_LOG, 		Material.STRIPPED_ACACIA_LOG, 	Material.STRIPPED_BIRCH_LOG,
			Material.STRIPPED_CRIMSON_STEM,	Material.STRIPPED_DARK_OAK_LOG,	Material.STRIPPED_JUNGLE_LOG,
			Material.STRIPPED_SPRUCE_LOG, 	Material.STRIPPED_WARPED_STEM
    );
	
	public static final Tag<Material> WORKSTATIONS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.BLAST_FURNACE, 	Material.SMOKER, 	
			Material.BARREL,		 	Material.LECTERN, 
			Material.STONECUTTER,		Material.LOOM,		Material.GRINDSTONE
    );
	
	public static final Tag<Material> DOORS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_DOOR, 		Material.ACACIA_DOOR, 	Material.BIRCH_DOOR,
			Material.CRIMSON_DOOR,	Material.DARK_OAK_DOOR,	Material.JUNGLE_DOOR,
			Material.SPRUCE_DOOR, 	Material.WARPED_DOOR,	Material.IRON_DOOR
    );
	
	public static final Tag<Material> FENCE_GATES = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_FENCE_GATE, 		Material.ACACIA_FENCE_GATE, 	Material.BIRCH_FENCE_GATE,
			Material.CRIMSON_FENCE_GATE,	Material.DARK_OAK_FENCE_GATE,	Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE, 	Material.WARPED_FENCE_GATE
    );
	
	public static final Tag<Material> CHESTS = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST
    );

    public static final Tag<Material> GLAZED_TERRACOTTA = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.WHITE_GLAZED_TERRACOTTA,	            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,	            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,	            Material.LIME_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA,	            Material.GRAY_GLAZED_TERRACOTTA,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,	        Material.CYAN_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,	            Material.BLUE_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,	            Material.GREEN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,		            Material.BLACK_GLAZED_TERRACOTTA
    );
    public static final Tag<Material> STAIRS = new MaterialSetTag(NamespacedKey.randomKey(),
    	Material.PURPUR_STAIRS,					Material.OAK_STAIRS,					Material.COBBLESTONE_STAIRS,
    	Material.BRICK_STAIRS,					Material.STONE_BRICK_STAIRS,			Material.NETHER_BRICK_STAIRS,
    	Material.SANDSTONE_STAIRS,				Material.SPRUCE_STAIRS,					Material.BIRCH_STAIRS,
    	Material.JUNGLE_STAIRS,					Material.CRIMSON_STAIRS,				Material.WARPED_STAIRS,
    	Material.QUARTZ_STAIRS,					Material.ACACIA_STAIRS,					Material.DARK_OAK_STAIRS,
    	Material.PRISMARINE_STAIRS,				Material.PRISMARINE_BRICK_STAIRS,		Material.DARK_PRISMARINE_STAIRS,
    	Material.RED_SANDSTONE_STAIRS,			Material.POLISHED_GRANITE_STAIRS,		Material.SMOOTH_RED_SANDSTONE_STAIRS,
    	Material.MOSSY_STONE_BRICK_STAIRS,		Material.POLISHED_DIORITE_STAIRS,		Material.MOSSY_COBBLESTONE_STAIRS,
    	Material.END_STONE_BRICK_STAIRS,		Material.STONE_STAIRS,					Material.SMOOTH_SANDSTONE_STAIRS,
    	Material.SMOOTH_QUARTZ_STAIRS,			Material.GRANITE_STAIRS,				Material.ANDESITE_STAIRS,
    	Material.RED_NETHER_BRICK_STAIRS,		Material.POLISHED_ANDESITE_STAIRS,		Material.DIORITE_STAIRS,
    	Material.BLACKSTONE_STAIRS,				Material.POLISHED_BLACKSTONE_STAIRS,	Material.POLISHED_BLACKSTONE_BRICK_STAIRS
    );
    public static final Tag<Material> SLABS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.PURPUR_SLAB,					Material.OAK_SLAB,					Material.COBBLESTONE_SLAB,
        	Material.BRICK_SLAB,					Material.STONE_BRICK_SLAB,			Material.NETHER_BRICK_SLAB,
        	Material.SANDSTONE_SLAB,				Material.SPRUCE_SLAB,				Material.BIRCH_SLAB,
        	Material.JUNGLE_SLAB,					Material.CRIMSON_SLAB,				Material.WARPED_SLAB,
        	Material.QUARTZ_SLAB,					Material.ACACIA_SLAB,				Material.DARK_OAK_SLAB,
        	Material.PRISMARINE_SLAB,				Material.PRISMARINE_BRICK_SLAB,		Material.DARK_PRISMARINE_SLAB,
        	Material.RED_SANDSTONE_SLAB,			Material.POLISHED_GRANITE_SLAB,		Material.SMOOTH_RED_SANDSTONE_SLAB,
        	Material.MOSSY_STONE_BRICK_SLAB,		Material.POLISHED_DIORITE_SLAB,		Material.MOSSY_COBBLESTONE_SLAB,
        	Material.END_STONE_BRICK_SLAB,			Material.STONE_SLAB,				Material.SMOOTH_SANDSTONE_SLAB,
        	Material.SMOOTH_QUARTZ_SLAB,			Material.GRANITE_SLAB,				Material.ANDESITE_SLAB,
        	Material.RED_NETHER_BRICK_SLAB,			Material.POLISHED_ANDESITE_SLAB,	Material.DIORITE_SLAB,
        	Material.BLACKSTONE_SLAB,				Material.POLISHED_BLACKSTONE_SLAB,	Material.POLISHED_BLACKSTONE_BRICK_SLAB
    );
    public static final Tag<Material> RAILS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.RAIL,
    		Material.ACTIVATOR_RAIL,
    		Material.POWERED_RAIL,
    		Material.DETECTOR_RAIL
    );
    public static final Tag<Material> BEDS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.WHITE_BED,    		Material.ORANGE_BED,    		Material.MAGENTA_BED,
    		Material.LIGHT_BLUE_BED,	Material.YELLOW_BED,			Material.LIME_BED,
    		Material.PINK_BED,		Material.GRAY_BED,	Material.LIGHT_GRAY_BED,
    		Material.CYAN_BED,	Material.PURPLE_BED, Material.BLUE_BED,
    		Material.BROWN_BED,	Material.GREEN_BED,	Material.RED_BED,
    		Material.BLACK_BED
    );
    public static final Tag<Material> TRAPDOORS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.IRON_TRAPDOOR,		Material.OAK_TRAPDOOR, 		Material.SPRUCE_TRAPDOOR,
    		Material.BIRCH_TRAPDOOR,	Material.JUNGLE_TRAPDOOR,	Material.ACACIA_TRAPDOOR,
    		Material.DARK_OAK_TRAPDOOR,	Material.CRIMSON_TRAPDOOR,	Material.WARPED_TRAPDOOR
    );
}