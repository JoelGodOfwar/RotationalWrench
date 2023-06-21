package com.github.joelgodofwar.rw.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

@SuppressWarnings("deprecation")
public class Tags_117 {
	public static final Tag<Material> ANVIL = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL
    ); // 1.11

	public static final Tag<Material> BANNER = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.WHITE_BANNER,    		Material.ORANGE_BANNER,			Material.MAGENTA_BANNER,
    		Material.LIGHT_BLUE_BANNER,		Material.YELLOW_BANNER,			Material.LIME_BANNER,
    		Material.PINK_BANNER,			Material.GRAY_BANNER,			Material.LIGHT_GRAY_BANNER,
    		Material.CYAN_BANNER,			Material.PURPLE_BANNER, 		Material.BLUE_BANNER,
    		Material.BROWN_BANNER,			Material.GREEN_BANNER,			Material.RED_BANNER,
    		Material.BLACK_BANNER
    ); // 1.11
	public static final Tag<Material> WALL_BANNER = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.WHITE_WALL_BANNER,    		Material.ORANGE_WALL_BANNER,			Material.MAGENTA_WALL_BANNER,
    		Material.LIGHT_BLUE_WALL_BANNER,		Material.YELLOW_WALL_BANNER,			Material.LIME_WALL_BANNER,
    		Material.PINK_WALL_BANNER,			Material.GRAY_WALL_BANNER,			Material.LIGHT_GRAY_WALL_BANNER,
    		Material.CYAN_WALL_BANNER,			Material.PURPLE_WALL_BANNER, 		Material.BLUE_WALL_BANNER,
    		Material.BROWN_WALL_BANNER,			Material.GREEN_WALL_BANNER,			Material.RED_WALL_BANNER,
    		Material.BLACK_WALL_BANNER
    ); // 1.11

	public static final Tag<Material> BASALT = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.POLISHED_BASALT
    ); // 1.11
    
    public static final Tag<Material> BEDS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.WHITE_BED,    		Material.ORANGE_BED,    		Material.MAGENTA_BED,
    		Material.LIGHT_BLUE_BED,	Material.YELLOW_BED,			Material.LIME_BED,
    		Material.PINK_BED,		Material.GRAY_BED,	Material.LIGHT_GRAY_BED,
    		Material.CYAN_BED,	Material.PURPLE_BED, Material.BLUE_BED,
    		Material.BROWN_BED,	Material.GREEN_BED,	Material.RED_BED,
    		Material.BLACK_BED
    ); // 1.8, 1.12
	
	public static final Tag<Material> BEE = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.BEE_NEST, Material.BEEHIVE
    ); // 1.11
	
	public static final Tag<Material> BELL = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.BELL
    ); // 1.14
	
	public static final Tag<Material> BONE = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.BONE_BLOCK
    ); // 1.11
	
	public static final Tag<Material> BUTTONS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.STONE_BUTTON,		Material.OAK_BUTTON,		Material.SPRUCE_BUTTON,
			Material.BIRCH_BUTTON,		Material.JUNGLE_BUTTON,		Material.ACACIA_BUTTON,
			Material.DARK_OAK_BUTTON,	Material.CRIMSON_BUTTON,	Material.WARPED_BUTTON,
			Material.POLISHED_BLACKSTONE_BUTTON
    ); // 1.11
	
	public static final Tag<Material> CAMPFIRES = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.CAMPFIRE, Material.SOUL_CAMPFIRE
    ); // 1.11
	
	public static final Tag<Material> CARVED_PUMPKIN = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN
    ); // 1.11
	
	public static final Tag<Material> CHESTS = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST
    ); // 1.11
	
	public static final Tag<Material> CORAL = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.DEAD_BRAIN_CORAL_FAN,	Material.DEAD_BUBBLE_CORAL_FAN,	Material.DEAD_FIRE_CORAL_FAN,
            Material.DEAD_HORN_CORAL_FAN,	Material.DEAD_TUBE_CORAL_FAN,
            
            Material.BRAIN_CORAL_FAN,	Material.BUBBLE_CORAL_FAN,	Material.FIRE_CORAL_FAN,
            Material.HORN_CORAL_FAN,	Material.TUBE_CORAL_FAN
    ); // 1.11
	
	public static final Tag<Material> CORAL_WALL = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.DEAD_BRAIN_CORAL_WALL_FAN,	Material.DEAD_BUBBLE_CORAL_WALL_FAN,	Material.DEAD_FIRE_CORAL_WALL_FAN,
            Material.DEAD_HORN_CORAL_WALL_FAN,	Material.DEAD_TUBE_CORAL_WALL_FAN,
            
            Material.BRAIN_CORAL_WALL_FAN,	Material.BUBBLE_CORAL_WALL_FAN,	Material.FIRE_CORAL_WALL_FAN,
            Material.HORN_CORAL_WALL_FAN,	Material.TUBE_CORAL_WALL_FAN
    ); // 1.11
	
	public static final Tag<Material> DOORS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_DOOR, 		Material.ACACIA_DOOR, 	Material.BIRCH_DOOR,
			Material.DARK_OAK_DOOR,	Material.JUNGLE_DOOR,
			Material.SPRUCE_DOOR,	Material.IRON_DOOR,
			Material.CRIMSON_DOOR,	Material.WARPED_DOOR
	); // 1.16
	
	public static final Tag<Material> LIGHTNING_ROD = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.LIGHTNING_ROD
    ); // 1.17
	
	public static final Tag<Material> END_ROD = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.END_ROD
    ); // 1.11
	
	public static final Tag<Material> FENCE_GATES = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_FENCE_GATE, 		Material.ACACIA_FENCE_GATE, 	Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,	Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE,
			Material.CRIMSON_FENCE_GATE,	Material.WARPED_FENCE_GATE
	); // 1.16

    public static final Tag<Material> GLAZED_TERRACOTTA = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.WHITE_GLAZED_TERRACOTTA,	            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,	            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,	            Material.LIME_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA,	            Material.GRAY_GLAZED_TERRACOTTA,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,	        Material.CYAN_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,	            Material.BLUE_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,	            Material.GREEN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,		            Material.BLACK_GLAZED_TERRACOTTA
    ); // 1.12
	
	public static final Tag<Material> HEADS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.PLAYER_HEAD, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, 
			Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD
    );
	
	public static final Tag<Material> HEADS2 = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.PLAYER_WALL_HEAD, Material.CREEPER_WALL_HEAD, Material.ZOMBIE_WALL_HEAD, 
			Material.SKELETON_WALL_SKULL, Material.WITHER_SKELETON_WALL_SKULL, Material.DRAGON_WALL_HEAD
    );
	
	public static final Tag<Material> LANTERNS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.LANTERN, Material.SOUL_LANTERN
    ); // 1.11
	
	public static final Tag<Material> LOGS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_LOG, 				Material.ACACIA_LOG, 			Material.BIRCH_LOG,
			Material.DARK_OAK_LOG,			Material.JUNGLE_LOG,			Material.SPRUCE_LOG,
			Material.STRIPPED_OAK_LOG, 		Material.STRIPPED_ACACIA_LOG, 	Material.STRIPPED_BIRCH_LOG,
			Material.STRIPPED_DARK_OAK_LOG,	Material.STRIPPED_JUNGLE_LOG,
			Material.STRIPPED_SPRUCE_LOG,
			Material.CRIMSON_STEM,	 		Material.WARPED_STEM,			Material.STRIPPED_CRIMSON_STEM,
			Material.STRIPPED_WARPED_STEM
	); // 1.16
	
	public static final Tag<Material> PILLARS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.PURPUR_PILLAR, Material.QUARTZ_PILLAR
    );
	
	public static final Tag<Material> PISTONS = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.STICKY_PISTON,	Material.PISTON
    ); // 1.11
    
    public static final Tag<Material> RAILS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.RAIL,
    		Material.ACTIVATOR_RAIL,
    		Material.POWERED_RAIL,
    		Material.DETECTOR_RAIL
    ); // 1.8
	
	public static final Tag<Material> REDSTONE_COMPONENTS = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.OBSERVER,      Material.DISPENSER,     Material.DROPPER,
            Material.HOPPER,        Material.FURNACE
    ); // 1.11
	
	public static final Tag<Material> REDSTONE_COMPONENTS2 = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.REPEATER,		Material.COMPARATOR
    ); // 1.11
	
	public static final Tag<Material> SHULKER = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.SHULKER_BOX,				Material.WHITE_SHULKER_BOX,			Material.ORANGE_SHULKER_BOX,
			Material.MAGENTA_SHULKER_BOX,		Material.LIGHT_BLUE_SHULKER_BOX,	Material.YELLOW_SHULKER_BOX,
			Material.LIME_SHULKER_BOX,			Material.PINK_SHULKER_BOX,			Material.GRAY_SHULKER_BOX,
			Material.LIGHT_GRAY_SHULKER_BOX,	Material.CYAN_SHULKER_BOX,			Material.PURPLE_SHULKER_BOX,
			Material.BLUE_SHULKER_BOX,			Material.BROWN_SHULKER_BOX,			Material.GREEN_SHULKER_BOX,
			Material.RED_SHULKER_BOX,			Material.BLACK_SHULKER_BOX
    );
	
	public static final Tag<Material> SIGNS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_SIGN,			Material.SPRUCE_SIGN,		Material.BIRCH_SIGN,
			Material.JUNGLE_SIGN,		Material.ACACIA_SIGN,		Material.DARK_OAK_SIGN,
			Material.CRIMSON_SIGN,		Material.WARPED_SIGN
    ); // 1.11
	
	public static final Tag<Material> SIGNS2 = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.OAK_WALL_SIGN,			Material.SPRUCE_WALL_SIGN,		Material.BIRCH_WALL_SIGN,
			Material.JUNGLE_WALL_SIGN,		Material.ACACIA_WALL_SIGN,		Material.DARK_OAK_WALL_SIGN,
			Material.CRIMSON_WALL_SIGN,		Material.WARPED_WALL_SIGN
    ); // 1.11
    
    public static final Tag<Material> SLABS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.PURPUR_SLAB,					Material.OAK_SLAB,							Material.COBBLESTONE_SLAB,
        	Material.BRICK_SLAB,					Material.STONE_BRICK_SLAB,					Material.NETHER_BRICK_SLAB,
        	Material.SPRUCE_SLAB,					Material.BIRCH_SLAB,						Material.JUNGLE_SLAB,					
        	Material.QUARTZ_SLAB,					Material.ACACIA_SLAB,						Material.DARK_OAK_SLAB,
        	Material.PRISMARINE_SLAB,				Material.PRISMARINE_BRICK_SLAB,				Material.DARK_PRISMARINE_SLAB,
        	Material.POLISHED_GRANITE_SLAB,			Material.SMOOTH_RED_SANDSTONE_SLAB,			Material.MOSSY_STONE_BRICK_SLAB,
        	Material.POLISHED_DIORITE_SLAB,			Material.MOSSY_COBBLESTONE_SLAB,			Material.END_STONE_BRICK_SLAB,
        	Material.STONE_SLAB,					Material.SMOOTH_SANDSTONE_SLAB,				Material.SMOOTH_QUARTZ_SLAB,
        	Material.GRANITE_SLAB,					Material.ANDESITE_SLAB,						Material.RED_NETHER_BRICK_SLAB,
        	Material.POLISHED_ANDESITE_SLAB,		Material.DIORITE_SLAB,
    		Material.SANDSTONE_SLAB,				Material.RED_SANDSTONE_SLAB,
    		Material.BLACKSTONE_SLAB,				Material.POLISHED_BLACKSTONE_SLAB,			Material.POLISHED_BLACKSTONE_BRICK_SLAB,
    		Material.CRIMSON_SLAB,					Material.WARPED_SLAB,
        	Material.DEEPSLATE_BRICK_SLAB,			Material.DEEPSLATE_TILE_SLAB,				Material.COBBLED_DEEPSLATE_SLAB,
        	Material.POLISHED_DEEPSLATE_SLAB,		Material.CUT_COPPER_SLAB,					Material.EXPOSED_CUT_COPPER_SLAB,
        	Material.WEATHERED_CUT_COPPER_SLAB,		Material.OXIDIZED_CUT_COPPER_SLAB,			Material.WAXED_CUT_COPPER_SLAB,
        	Material.WAXED_EXPOSED_CUT_COPPER_SLAB,	Material.WAXED_WEATHERED_CUT_COPPER_SLAB,	Material.WAXED_OXIDIZED_CUT_COPPER_SLAB
    ); // 1.16
    
    public static final Tag<Material> STAIRS = new MaterialSetTag(NamespacedKey.randomKey(),
    	Material.PURPUR_STAIRS,						Material.OAK_STAIRS,						Material.COBBLESTONE_STAIRS,
    	Material.BRICK_STAIRS,						Material.STONE_BRICK_STAIRS,				Material.NETHER_BRICK_STAIRS,
    	Material.SANDSTONE_STAIRS,					Material.SPRUCE_STAIRS,						Material.BIRCH_STAIRS,
    	Material.JUNGLE_STAIRS,						Material.QUARTZ_STAIRS,						Material.ACACIA_STAIRS,
    	Material.DARK_OAK_STAIRS,					Material.PRISMARINE_STAIRS,					Material.PRISMARINE_BRICK_STAIRS,
    	Material.DARK_PRISMARINE_STAIRS,			Material.RED_SANDSTONE_STAIRS,				Material.POLISHED_GRANITE_STAIRS,
    	Material.SMOOTH_RED_SANDSTONE_STAIRS,		Material.MOSSY_STONE_BRICK_STAIRS,			Material.POLISHED_DIORITE_STAIRS,
    	Material.MOSSY_COBBLESTONE_STAIRS,			Material.END_STONE_BRICK_STAIRS,			Material.STONE_STAIRS,
    	Material.SMOOTH_SANDSTONE_STAIRS,			Material.SMOOTH_QUARTZ_STAIRS,				Material.GRANITE_STAIRS,
    	Material.ANDESITE_STAIRS,					Material.RED_NETHER_BRICK_STAIRS,			Material.POLISHED_ANDESITE_STAIRS,
    	Material.DIORITE_STAIRS,
    	Material.BLACKSTONE_STAIRS,					Material.POLISHED_BLACKSTONE_STAIRS,		Material.POLISHED_BLACKSTONE_BRICK_STAIRS,
    	Material.CRIMSON_STAIRS,					Material.WARPED_STAIRS,
    	Material.DEEPSLATE_BRICK_STAIRS,			Material.DEEPSLATE_TILE_STAIRS,				Material.COBBLED_DEEPSLATE_STAIRS,
    	Material.POLISHED_DEEPSLATE_STAIRS,			Material.CUT_COPPER_STAIRS,					Material.EXPOSED_CUT_COPPER_STAIRS,
    	Material.WEATHERED_CUT_COPPER_STAIRS,		Material.OXIDIZED_CUT_COPPER_STAIRS,		Material.WAXED_CUT_COPPER_STAIRS,
    	Material.WAXED_EXPOSED_CUT_COPPER_STAIRS,	Material.WAXED_WEATHERED_CUT_COPPER_STAIRS,	Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS
    ); // 1.16
	
	public static final Tag<Material> TORCH = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.WALL_TORCH	,	Material.REDSTONE_WALL_TORCH,
			Material.TORCH	,		Material.REDSTONE_TORCH
    ); // 1.11
	
	public static final Tag<Material> TORCH2 = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.SOUL_WALL_TORCH,	 Material.SOUL_TORCH
    ); // 1.11
    
    public static final Tag<Material> TRAPDOORS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.IRON_TRAPDOOR,		Material.OAK_TRAPDOOR, 		Material.SPRUCE_TRAPDOOR,
    		Material.BIRCH_TRAPDOOR,	Material.JUNGLE_TRAPDOOR,	Material.ACACIA_TRAPDOOR,
    		Material.DARK_OAK_TRAPDOOR,	
    		Material.CRIMSON_TRAPDOOR,	Material.WARPED_TRAPDOOR
    ); // 1.16
	
	public static final Tag<Material> WORKSTATIONS = new MaterialSetTag(NamespacedKey.randomKey(),
			Material.BLAST_FURNACE, 	Material.SMOKER, 	
			Material.BARREL,		 	Material.LECTERN, 
			Material.LOOM,				Material.STONECUTTER,		Material.GRINDSTONE
    ); // 1.14, 1.16
    
    public static final Tag<Material> NO_BUTTONS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.ARMOR_STAND,
    		Material.WHITE_BANNER,    		Material.ORANGE_BANNER,			Material.MAGENTA_BANNER,
    		Material.LIGHT_BLUE_BANNER,		Material.YELLOW_BANNER,			Material.LIME_BANNER,
    		Material.PINK_BANNER,			Material.GRAY_BANNER,			Material.LIGHT_GRAY_BANNER,
    		Material.CYAN_BANNER,			Material.PURPLE_BANNER, 		Material.BLUE_BANNER,
    		Material.BROWN_BANNER,			Material.GREEN_BANNER,			Material.RED_BANNER,
    		Material.BLACK_BANNER,
    		Material.BELL,					Material.BREWING_STAND,			Material.CACTUS,
    		Material.WHITE_CARPET,    		Material.ORANGE_CARPET,			Material.MAGENTA_CARPET,
    		Material.LIGHT_BLUE_CARPET,		Material.YELLOW_CARPET,			Material.LIME_CARPET,
    		Material.PINK_CARPET,			Material.GRAY_CARPET,			Material.LIGHT_GRAY_CARPET,
    		Material.CYAN_CARPET,			Material.PURPLE_CARPET, 		Material.BLUE_CARPET,
    		Material.BROWN_CARPET,			Material.GREEN_CARPET,			Material.RED_CARPET,
    		Material.BLACK_CARPET,			Material.CAULDRON,
    		Material.CHORUS_PLANT,			Material.COBWEB,				Material.END_ROD,
    		Material.CHEST, 				Material.ENDER_CHEST,				Material.TRAPPED_CHEST,
    		Material.HOPPER, 				Material.ANVIL,						Material.CHIPPED_ANVIL,
    		Material.DAMAGED_ANVIL,			Material.GRINDSTONE, 				Material.STONECUTTER,
    		Material.ENCHANTING_TABLE,		Material.END_PORTAL_FRAME,			Material.HONEY_BLOCK,				
    		Material.COBBLESTONE_WALL,	    Material.BRICK_WALL,				Material.STONE_BRICK_WALL,
    		Material.NETHER_BRICK_WALL,	    Material.SANDSTONE_WALL,			Material.PRISMARINE_WALL,
    		Material.RED_SANDSTONE_WALL,	Material.MOSSY_STONE_BRICK_WALL,	Material.MOSSY_COBBLESTONE_WALL,
    		Material.END_STONE_BRICK_WALL,	Material.GRANITE_WALL,				
        	Material.ANDESITE_WALL,			Material.RED_NETHER_BRICK_WALL,		Material.DIORITE_WALL,
        	Material.BLACKSTONE_WALL,		Material.POLISHED_BLACKSTONE_WALL,	Material.POLISHED_BLACKSTONE_BRICK_WALL,
        	Material.OAK_FENCE,				Material.NETHER_BRICK_FENCE,		Material.SPRUCE_FENCE,
        	Material.BIRCH_FENCE,			Material.JUNGLE_FENCE,				Material.ACACIA_FENCE,
        	Material.DARK_OAK_FENCE,		Material.CRIMSON_FENCE,				Material.WARPED_FENCE,
        	Material.LANTERN,				Material.SOUL_LANTERN,				Material.CAMPFIRE,
        	Material.SOUL_CAMPFIRE,			Material.CHAIN,						
        	Material.OAK_LEAVES, 			Material.ACACIA_LEAVES, 			Material.BIRCH_LEAVES,
			Material.DARK_OAK_LEAVES,		Material.JUNGLE_LEAVES,				Material.SPRUCE_LEAVES,
			Material.LECTERN,
			Material.WHITE_BED,    			Material.ORANGE_BED,				Material.MAGENTA_BED,
    		Material.LIGHT_BLUE_BED,		Material.YELLOW_BED,				Material.LIME_BED,
    		Material.PINK_BED,				Material.GRAY_BED,					Material.LIGHT_GRAY_BED,
    		Material.CYAN_BED,				Material.PURPLE_BED, 				Material.BLUE_BED,
    		Material.BROWN_BED,				Material.GREEN_BED,					Material.RED_BED,
    		Material.BLACK_BED,				Material.IRON_BARS,
    		Material.OAK_FENCE_GATE, 		Material.ACACIA_FENCE_GATE, 		Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,	Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE,
			Material.CRIMSON_FENCE_GATE,	Material.WARPED_FENCE_GATE,
			Material.DAYLIGHT_DETECTOR,		Material.FLOWER_POT,
			Material.WHITE_STAINED_GLASS_PANE,    		Material.ORANGE_STAINED_GLASS_PANE,			Material.MAGENTA_STAINED_GLASS_PANE,
    		Material.LIGHT_BLUE_STAINED_GLASS_PANE,		Material.YELLOW_STAINED_GLASS_PANE,			Material.LIME_STAINED_GLASS_PANE,
    		Material.PINK_STAINED_GLASS_PANE,			Material.GRAY_STAINED_GLASS_PANE,			Material.LIGHT_GRAY_STAINED_GLASS_PANE,
    		Material.CYAN_STAINED_GLASS_PANE,			Material.PURPLE_STAINED_GLASS_PANE, 		Material.BLUE_STAINED_GLASS_PANE,
    		Material.BROWN_STAINED_GLASS_PANE,			Material.GREEN_STAINED_GLASS_PANE,			Material.RED_STAINED_GLASS_PANE,
    		Material.BLACK_STAINED_GLASS_PANE,
    		Material.PLAYER_HEAD,			Material.CREEPER_HEAD,			Material.ZOMBIE_HEAD, 
			Material.SKELETON_SKULL,		Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD,
			Material.LILY_PAD,				Material.SCAFFOLDING,
    		Material.BAMBOO,				Material.BAMBOO_SAPLING,
    		Material.WHITE_WALL_BANNER,    		Material.ORANGE_WALL_BANNER,			Material.MAGENTA_WALL_BANNER,
    		Material.LIGHT_BLUE_WALL_BANNER,		Material.YELLOW_WALL_BANNER,			Material.LIME_WALL_BANNER,
    		Material.PINK_WALL_BANNER,			Material.GRAY_WALL_BANNER,			Material.LIGHT_GRAY_WALL_BANNER,
    		Material.CYAN_WALL_BANNER,			Material.PURPLE_WALL_BANNER, 		Material.BLUE_WALL_BANNER,
    		Material.BROWN_WALL_BANNER,			Material.GREEN_WALL_BANNER,			Material.RED_WALL_BANNER,
    		Material.BLACK_WALL_BANNER,
    		Material.WHITE_BANNER,    		Material.ORANGE_BANNER,			Material.MAGENTA_BANNER,
    		Material.LIGHT_BLUE_BANNER,		Material.YELLOW_BANNER,			Material.LIME_BANNER,
    		Material.PINK_BANNER,			Material.GRAY_BANNER,			Material.LIGHT_GRAY_BANNER,
    		Material.CYAN_BANNER,			Material.PURPLE_BANNER, 		Material.BLUE_BANNER,
    		Material.BROWN_BANNER,			Material.GREEN_BANNER,			Material.RED_BANNER,
    		Material.BLACK_BANNER
        	
    ); // 1.16
    
    public static final Tag<Material> NO_LANTERNS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST,
    		Material.HOPPER, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL,
    		Material.GRINDSTONE, Material.STONECUTTER, Material.ENCHANTING_TABLE,
    		Material.END_PORTAL_FRAME,	Material.HONEY_BLOCK
    );
    
    public static final Tag<Material> NO_TORCH_SIDE = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.ANVIL,					Material.CHIPPED_ANVIL,    		Material.DAMAGED_ANVIL,
    		Material.WHITE_BANNER,    		Material.ORANGE_BANNER,			Material.MAGENTA_BANNER,
    		Material.LIGHT_BLUE_BANNER,		Material.YELLOW_BANNER,			Material.LIME_BANNER,
    		Material.PINK_BANNER,			Material.GRAY_BANNER,			Material.LIGHT_GRAY_BANNER,
    		Material.CYAN_BANNER,			Material.PURPLE_BANNER, 		Material.BLUE_BANNER,
    		Material.BROWN_BANNER,			Material.GREEN_BANNER,			Material.RED_BANNER,
    		Material.BLACK_BANNER,			
    		Material.WHITE_BED,    			Material.ORANGE_BED,			Material.MAGENTA_BED,
    		Material.LIGHT_BLUE_BED,		Material.YELLOW_BED,			Material.LIME_BED,
    		Material.PINK_BED,				Material.GRAY_BED,				Material.LIGHT_GRAY_BED,
    		Material.CYAN_BED,				Material.PURPLE_BED, 			Material.BLUE_BED,
    		Material.BROWN_BED,				Material.GREEN_BED,				Material.RED_BED,
    		Material.BLACK_BED,
    		Material.BELL,					Material.BREWING_STAND,			Material.CACTUS,
    		Material.CAMPFIRE,				Material.SOUL_CAMPFIRE,			Material.CAULDRON,
    		Material.WHITE_CARPET,    		Material.ORANGE_CARPET,			Material.MAGENTA_CARPET,
    		Material.LIGHT_BLUE_CARPET,		Material.YELLOW_CARPET,			Material.LIME_CARPET,
    		Material.PINK_CARPET,			Material.GRAY_CARPET,			Material.LIGHT_GRAY_CARPET,
    		Material.CYAN_CARPET,			Material.PURPLE_CARPET, 		Material.BLUE_CARPET,
    		Material.BROWN_CARPET,			Material.GREEN_CARPET,			Material.RED_CARPET,
    		Material.BLACK_CARPET,
    		Material.CHEST, 				Material.CHORUS_PLANT,			Material.COBWEB,
    		Material.DAYLIGHT_DETECTOR,		Material.ENCHANTING_TABLE,		Material.END_ROD,
    		Material.ENDER_CHEST,			Material.END_PORTAL_FRAME,		Material.FLOWER_POT,
    		Material.OAK_FENCE, 			Material.ACACIA_FENCE,		 	Material.BIRCH_FENCE,
			Material.DARK_OAK_FENCE,		Material.JUNGLE_FENCE,			Material.SPRUCE_FENCE,
			Material.CRIMSON_FENCE,			Material.WARPED_FENCE,
    		Material.OAK_FENCE_GATE, 		Material.ACACIA_FENCE_GATE, 	Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,	Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE,
			Material.CRIMSON_FENCE_GATE,	Material.WARPED_FENCE_GATE,
    		Material.DIRT_PATH,				Material.GRINDSTONE,			Material.IRON_BARS,
    		Material.LANTERN,				Material.SOUL_LANTERN,
    		Material.OAK_LEAVES, 			Material.ACACIA_LEAVES, 		Material.BIRCH_LEAVES,
			Material.DARK_OAK_LEAVES,		Material.JUNGLE_LEAVES,			Material.SPRUCE_LEAVES,
			Material.LECTERN,				Material.LILY_PAD,				Material.HONEY_BLOCK,
			Material.HOPPER,				Material.SCAFFOLDING,			Material.STONECUTTER,
			Material.TRAPPED_CHEST,			Material.IRON_BARS,				Material.CHAIN,
			Material.GLASS_PANE,			
			Material.PLAYER_HEAD,			Material.CREEPER_HEAD,			Material.ZOMBIE_HEAD, 
			Material.SKELETON_SKULL,		Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD,
			Material.WHITE_STAINED_GLASS_PANE,    		Material.ORANGE_STAINED_GLASS_PANE,			Material.MAGENTA_STAINED_GLASS_PANE,
    		Material.LIGHT_BLUE_STAINED_GLASS_PANE,		Material.YELLOW_STAINED_GLASS_PANE,			Material.LIME_STAINED_GLASS_PANE,
    		Material.PINK_STAINED_GLASS_PANE,			Material.GRAY_STAINED_GLASS_PANE,			Material.LIGHT_GRAY_STAINED_GLASS_PANE,
    		Material.CYAN_STAINED_GLASS_PANE,			Material.PURPLE_STAINED_GLASS_PANE, 		Material.BLUE_STAINED_GLASS_PANE,
    		Material.BROWN_STAINED_GLASS_PANE,			Material.GREEN_STAINED_GLASS_PANE,			Material.RED_STAINED_GLASS_PANE,
    		Material.BLACK_STAINED_GLASS_PANE,
    		Material.BAMBOO,				Material.BAMBOO_SAPLING,		Material.FARMLAND
			
    ); // 1.16
    
    public static final Tag<Material> NO_TORCH_TOP = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.ARMOR_STAND,
    		Material.WHITE_BANNER,    		Material.ORANGE_BANNER,			Material.MAGENTA_BANNER,
    		Material.LIGHT_BLUE_BANNER,		Material.YELLOW_BANNER,			Material.LIME_BANNER,
    		Material.PINK_BANNER,			Material.GRAY_BANNER,			Material.LIGHT_GRAY_BANNER,
    		Material.CYAN_BANNER,			Material.PURPLE_BANNER, 		Material.BLUE_BANNER,
    		Material.BROWN_BANNER,			Material.GREEN_BANNER,			Material.RED_BANNER,
    		Material.BLACK_BANNER,
    		Material.WHITE_BED,    			Material.ORANGE_BED,			Material.MAGENTA_BED,
    		Material.LIGHT_BLUE_BED,		Material.YELLOW_BED,			Material.LIME_BED,
    		Material.PINK_BED,				Material.GRAY_BED,				Material.LIGHT_GRAY_BED,
    		Material.CYAN_BED,				Material.PURPLE_BED, 			Material.BLUE_BED,
    		Material.BROWN_BED,				Material.GREEN_BED,				Material.RED_BED,
    		Material.BLACK_BED,
    		Material.BREWING_STAND,			Material.CACTUS,
    		Material.CAMPFIRE,				Material.SOUL_CAMPFIRE,			Material.CAULDRON,
    		Material.WHITE_CARPET,    		Material.ORANGE_CARPET,			Material.MAGENTA_CARPET,
    		Material.LIGHT_BLUE_CARPET,		Material.YELLOW_CARPET,			Material.LIME_CARPET,
    		Material.PINK_CARPET,			Material.GRAY_CARPET,			Material.LIGHT_GRAY_CARPET,
    		Material.CYAN_CARPET,			Material.PURPLE_CARPET, 		Material.BLUE_CARPET,
    		Material.BROWN_CARPET,			Material.GREEN_CARPET,			Material.RED_CARPET,
    		Material.BLACK_CARPET,
    		Material.CHEST, 				Material.CHORUS_PLANT,			Material.COBWEB,
    		Material.COMPOSTER,				Material.DAYLIGHT_DETECTOR,		Material.ENCHANTING_TABLE,
    		Material.ENDER_CHEST,			Material.END_PORTAL_FRAME,		Material.FLOWER_POT,
    		Material.OAK_FENCE_GATE, 		Material.ACACIA_FENCE_GATE, 	Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,	Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE,
			Material.CRIMSON_FENCE_GATE,	Material.WARPED_FENCE_GATE,
    		Material.DIRT_PATH,							
    		Material.LANTERN,				Material.SOUL_LANTERN,
    		Material.OAK_LEAVES, 			Material.ACACIA_LEAVES, 		Material.BIRCH_LEAVES,
			Material.DARK_OAK_LEAVES,		Material.JUNGLE_LEAVES,			Material.SPRUCE_LEAVES,
			Material.LECTERN,				Material.LILY_PAD,				Material.HONEY_BLOCK,
			Material.HOPPER,				Material.STONECUTTER,			Material.TRAPPED_CHEST,
			Material.PLAYER_HEAD,			Material.CREEPER_HEAD,			Material.ZOMBIE_HEAD, 
			Material.SKELETON_SKULL,		Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD,
    		Material.BAMBOO,				Material.BAMBOO_SAPLING
			
    ); // 1.16
    
    public static final Tag<Material> YES_SIGNS = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.ANVIL, 			Material.CHIPPED_ANVIL, 	Material.DAMAGED_ANVIL,
    		Material.ENCHANTING_TABLE,	Material.GRINDSTONE,
    		Material.LANTERN, 			Material.SOUL_LANTERN
    );
    
    public static final Tag<Material> NO_SIGNS_SIDE = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.ARMOR_STAND,			Material.CACTUS,
    		Material.WHITE_CARPET,    		Material.ORANGE_CARPET,			Material.MAGENTA_CARPET,
    		Material.LIGHT_BLUE_CARPET,		Material.YELLOW_CARPET,			Material.LIME_CARPET,
    		Material.PINK_CARPET,			Material.GRAY_CARPET,			Material.LIGHT_GRAY_CARPET,
    		Material.CYAN_CARPET,			Material.PURPLE_CARPET, 		Material.BLUE_CARPET,
    		Material.BROWN_CARPET,			Material.GREEN_CARPET,			Material.RED_CARPET,
    		Material.BLACK_CARPET,			Material.CHORUS_FLOWER,			Material.CHORUS_PLANT,
    		Material.FLOWER_POT,			Material.LILY_PAD,				Material.SCAFFOLDING,
    		Material.PLAYER_HEAD,			Material.CREEPER_HEAD,			Material.ZOMBIE_HEAD, 
			Material.SKELETON_SKULL,		Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD,
			Material.STONE_BUTTON,			Material.OAK_BUTTON,			Material.SPRUCE_BUTTON,
			Material.BIRCH_BUTTON,			Material.JUNGLE_BUTTON,			Material.ACACIA_BUTTON,
			Material.DARK_OAK_BUTTON,		Material.CRIMSON_BUTTON,		Material.WARPED_BUTTON,
			Material.POLISHED_BLACKSTONE_BUTTON,
			Material.LEVER,					Material.END_ROD,
			Material.TORCH,					Material.SOUL_TORCH,			Material.REDSTONE_TORCH,
			Material.WALL_TORCH,			Material.SOUL_WALL_TORCH,		Material.REDSTONE_WALL_TORCH
    );
    
    public static final Tag<Material> NO_SIGNS_TOP = new MaterialSetTag(NamespacedKey.randomKey(),
    		Material.ARMOR_STAND,			
    		Material.WHITE_BANNER,    		Material.ORANGE_BANNER,			Material.MAGENTA_BANNER,
    		Material.LIGHT_BLUE_BANNER,		Material.YELLOW_BANNER,			Material.LIME_BANNER,
    		Material.PINK_BANNER,			Material.GRAY_BANNER,			Material.LIGHT_GRAY_BANNER,
    		Material.CYAN_BANNER,			Material.PURPLE_BANNER, 		Material.BLUE_BANNER,
    		Material.BROWN_BANNER,			Material.GREEN_BANNER,			Material.RED_BANNER,
    		Material.BLACK_BANNER,
    		Material.CACTUS,
    		Material.WHITE_CARPET,    		Material.ORANGE_CARPET,			Material.MAGENTA_CARPET,
    		Material.LIGHT_BLUE_CARPET,		Material.YELLOW_CARPET,			Material.LIME_CARPET,
    		Material.PINK_CARPET,			Material.GRAY_CARPET,			Material.LIGHT_GRAY_CARPET,
    		Material.CYAN_CARPET,			Material.PURPLE_CARPET, 		Material.BLUE_CARPET,
    		Material.BROWN_CARPET,			Material.GREEN_CARPET,			Material.RED_CARPET,
    		Material.BLACK_CARPET,			Material.CHORUS_FLOWER,			Material.CHORUS_PLANT,
    		Material.FLOWER_POT,			Material.LILY_PAD,				Material.SCAFFOLDING,
    		Material.PLAYER_HEAD,			Material.CREEPER_HEAD,			Material.ZOMBIE_HEAD, 
			Material.SKELETON_SKULL,		Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD,
			Material.STONE_BUTTON,			Material.OAK_BUTTON,			Material.SPRUCE_BUTTON,
			Material.BIRCH_BUTTON,			Material.JUNGLE_BUTTON,			Material.ACACIA_BUTTON,
			Material.DARK_OAK_BUTTON,		Material.CRIMSON_BUTTON,		Material.WARPED_BUTTON,
			Material.POLISHED_BLACKSTONE_BUTTON,
			Material.LEVER,					Material.DIRT_PATH,
			Material.TORCH,					Material.SOUL_TORCH,			Material.REDSTONE_TORCH,
			Material.WALL_TORCH,			Material.SOUL_WALL_TORCH,		Material.REDSTONE_WALL_TORCH
    );
    
}