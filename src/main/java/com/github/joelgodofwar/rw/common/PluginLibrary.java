package com.github.joelgodofwar.rw.common;

import java.util.List;

import lib.github.joelgodofwar.coreutils.util.Validate;
import org.bukkit.plugin.Plugin;

import com.github.joelgodofwar.rw.common.error.BasicErrorReporter;
import com.github.joelgodofwar.rw.common.error.ErrorReporter;
import com.github.joelgodofwar.rw.common.error.ReportType;
import com.google.common.collect.ImmutableList;

public class PluginLibrary {

	/**
	 * The date (with ISO 8601 or YYYY-MM-DD) when the most recent version (1.20.4) was released.
	 */
	public static final String MINECRAFT_LAST_RELEASE_DATE = "2023-12-07";

	/**
	 * Plugins that are currently incompatible with MoreMobHeads.
	 */
	public static final List<String> INCOMPATIBLE = ImmutableList.of("");

	private static Plugin plugin;

	private static boolean updatesDisabled;
	private static boolean initialized;
	private static ErrorReporter reporter = new BasicErrorReporter();

	protected static void init(Plugin plugin, ErrorReporter reporter) {
		Validate.isTrue(!initialized, "MoreMobHeads has already been initialized.");
		PluginLibrary.plugin = plugin;
		PluginLibrary.reporter = reporter;

		initialized = true;
	}

	public static final ReportType REPORT_CANNOT_DELETE_CONFIG = new ReportType("Cannot delete old RotationalWrench configuration.");
	public static final ReportType REPORT_CANNOT_COPY_FILE = new ReportType("Cannot copy file.");

	public static final ReportType REPORT_PLUGIN_LOAD_ERROR = new ReportType("Cannot load RotationalWrench.");
	public static final ReportType REPORT_CANNOT_LOAD_CONFIG = new ReportType("Cannot load configuration");
	public static final ReportType REPORT_CANNOT_CHECK_CONFIG = new ReportType("Cannot check configuration");
	public static final ReportType REPORT_CANNOT_SAVE_CONFIG = new ReportType("Cannot save configuration");
	public static final ReportType REPORT_PLUGIN_ENABLE_ERROR = new ReportType("Cannot enable RotationalWrench.");
	public static final ReportType REPORT_PLUGIN_UNKNOWN_ERROR = new ReportType("Unknown Error");

	public static final ReportType REPORT_MESSAGES_LOAD_ERROR = new ReportType("Could not load messages.yml");
	public static final ReportType REPORT_MESSAGES_COPY_ERROR = new ReportType("Could not copy messages.yml to old_messages.yml");
	public static final ReportType REPORT_OLDMESSAGES_LOAD_ERROR = new ReportType("Could not load old_messages.yml");
	public static final ReportType REPORT_OLDMESSAGES_SAVE_ERROR = new ReportType("Could not save old messages to messages.yml");
	public static final ReportType REPORT_PLAYERHEAD_LOAD_ERROR = new ReportType("Could not load player_heads.yml");
	public static final ReportType REPORT_CUSTOM_LOAD_ERROR = new ReportType("Could not load custom_trades.yml");
	public static final ReportType REPORT_CHANCE_LOAD_ERROR = new ReportType("Could not load chance_config.yml");
	public static final ReportType REPORT_CHANCE_SAVE_ERROR = new ReportType("Could not save chance_config.yml");
	public static final ReportType REPORT_CHANCE_COPY_ERROR = new ReportType("Could not copy chance_config.yml to old_chance_config.yml");
	public static final ReportType REPORT_MOBNAMES_LOAD_ERROR = new ReportType("Could not load lang_mobnames file");

	public static final ReportType REPORT_PIE_LOAD_ERROR = new ReportType("Error with naming mob.");
	public static final ReportType REPORT_HEAD_URL_ERROR = new ReportType("Malformed URL Error");
	public static final ReportType REPORT_BLOCKHEAD_LOAD_ERROR = new ReportType("Cannot load BlockHeads file");

	public static final ReportType REPORT_METRICS_IO_ERROR = new ReportType("Unable to enable metrics due to network problems.");
	public static final ReportType REPORT_METRICS_GENERIC_ERROR = new ReportType("Unable to enable metrics due to network problems.");

	public static final ReportType REPORT_CANNOT_PARSE_MINECRAFT_VERSION = new ReportType("Unable to retrieve current Minecraft version. Assuming %s");
	public static final ReportType REPORT_CANNOT_DETECT_CONFLICTING_PLUGINS = new ReportType("Unable to detect conflicting plugin versions.");
	public static final ReportType REPORT_CANNOT_REGISTER_COMMAND = new ReportType("Cannot register command %s: %s");

	public static final ReportType REPORT_CANNOT_CREATE_TIMEOUT_TASK = new ReportType("Unable to create packet timeout task.");
	public static final ReportType REPORT_CANNOT_UPDATE_PLUGIN = new ReportType("Cannot perform automatic updates.");
	public static final ReportType REPORT_METRICS_LOAD_ERROR = new ReportType("Cannot load bStats Metrics.");

	public static final ReportType REPORT_CANNOT_GET_HEXNICK = new ReportType("Cannot get Nick from HexNicks.");
	public static final ReportType REPORT_CANNOT_LOAD_RECIPES = new ReportType("Cannot load recipes.");

	public static final ReportType ERROR_CANT_ROTATE_CORALFAN = new ReportType("Cannot rotate coral fan.");
	public static final ReportType ERROR_CANT_ROTATE_RAILS = new ReportType("Cannot rotate rails.");
	public static final ReportType ERROR_CANT_ROTATE_TRAPDOORS = new ReportType("Cannot rotate trapdoors.");
	public static final ReportType ERROR_CANT_ROTATE_GRINDSTONES = new ReportType("Cannot rotate grindstones.");
	public static final ReportType ERROR_CANT_ROTATE_CHAINS = new ReportType("Cannot rotate chains.");
	public static final ReportType ERROR_CANT_ROTATE_BEDSS = new ReportType("Cannot rotate beds.");
	public static final ReportType ERROR_CANT_ROTATE_SLABS = new ReportType("Cannot rotate slabs.");
	public static final ReportType ERROR_CANT_INVERT_STAIRS = new ReportType("Cannot invert stairs.");
	public static final ReportType ERROR_CANT_ROTATE_STAIRS = new ReportType("Cannot rotate stairs.");
	public static final ReportType ERROR_CANT_ROTATE_LOGSS = new ReportType("Cannot rotate Logs, Pillars, Basalt, or Bone.");
	public static final ReportType ERROR_CANT_ROTATE_WALLSIGNS = new ReportType("Cannot rotate Wall Signs/Banners.");
	public static final ReportType ERROR_CANT_ROTATE_HANGINGSIGNS = new ReportType("Cannot rotate Hanging Signs/Banners.");
	public static final ReportType ERROR_CANT_ROTATE_SIGNS = new ReportType("Cannot rotate Signs/Banners.");
	public static final ReportType ERROR_CANT_ROTATE_HEADS = new ReportType("Cannot rotate Heads.");
	public static final ReportType ERROR_CANT_ROTATE_WALL_HEADS = new ReportType("Cannot rotate Wall Heads.");
	public static final ReportType ERROR_CANT_ROTATE_CHESTS = new ReportType("Cannot rotate Heads.");
	public static final ReportType ERROR_CANT_ROTATE_BUTTONS = new ReportType("Cannot rotate Buttons/Levers.");
	public static final ReportType ERROR_CANT_ROTATE_BELLS = new ReportType("Cannot rotate Bells.");
	public static final ReportType ERROR_CANT_ROTATE_TORCHES = new ReportType("Cannot rotate Torches.");
	public static final ReportType ERROR_CANT_ROTATE_LADDERS = new ReportType("Cannot rotate Ladders/Tripwire Hooks.");
	public static final ReportType ERROR_CANT_ROTATE_LANTERNS = new ReportType("Cannot rotate Lanterns.");
	public static final ReportType ERROR_CANT_ROTATE_PISTONS = new ReportType("Cannot rotate Pistons.");
	public static final ReportType ERROR_CANT_ROTATE_WALLS = new ReportType("Cannot rotate Walls.");
	public static final ReportType ERROR_CANT_ROTATE_REPEATERS = new ReportType("Cannot rotate Repeaters/Comparators.");
	public static final ReportType ERROR_CANT_ROTATE_CRAFTERS = new ReportType("Cannot rotate Crafters.");
	public static final ReportType ERROR_CANT_ROTATE_RDIRECTIONAL_BLOCKS = new ReportType("Cannot rotate Directional Blocks.");
	public static final ReportType ERROR_CANT_ATTACH_HANGINGSIGNS = new ReportType("Cannot Attach Hanging Signs/Banners.");
	public static final ReportType ERROR_CANT_ATTACH_ARMORSTANDS = new ReportType("Cannot rotate Armorstands.");

	public static final ReportType ERROR_UNHANDLED_EXCEPTION = new ReportType("An unhandled exception occurred.");

	/**
	 * Gets the MoreMobHeads plugin instance.
	 * @return The plugin instance
	 */
	public static Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Disables the MoreMobHeads update checker.
	 */
	public static void disableUpdates() {
		updatesDisabled = true;
	}

	/**
	 * Retrieve the current error reporter.
	 * @return Current error reporter.
	 */
	public static ErrorReporter getErrorReporter() {
		return reporter;
	}

	/**
	 * Whether updates are currently disabled.
	 * @return True if it is, false if not
	 */
	public static boolean updatesDisabled() {
		return updatesDisabled;
	}
}