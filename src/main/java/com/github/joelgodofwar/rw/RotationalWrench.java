package com.github.joelgodofwar.rw;

import com.github.joelgodofwar.rw.commands.RWCommandHandler;
import com.github.joelgodofwar.rw.common.PluginLibrary;
import com.github.joelgodofwar.rw.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.rw.common.error.Report;
import com.github.joelgodofwar.rw.events.CraftItemEventHandler;
import com.github.joelgodofwar.rw.events.PlayerJoinEventHandler;
import com.github.joelgodofwar.rw.handlers.*;
import com.github.joelgodofwar.rw.i18n.Translator;
import com.github.joelgodofwar.rw.util.FileStuff;
import com.github.joelgodofwar.rw.util.Metrics;
import com.github.joelgodofwar.rw.util.Tags;
import com.google.common.collect.Lists;
import lib.github.joelgodofwar.coreutils.CoreUtils;
import lib.github.joelgodofwar.coreutils.util.JsonMessageUtils;
import lib.github.joelgodofwar.coreutils.util.Version;
import lib.github.joelgodofwar.coreutils.util.VersionChecker;
import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class RotationalWrench extends JavaPlugin{
	/** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	//public final static Logger logger = Logger.getLogger("Minecraft");
	public static String THIS_NAME;
	public static String THIS_VERSION;
	/** update checker variables */
	public int projectID = 85119; // https://spigotmc.org/resources/71236
	public String githubURL = "https://github.com/JoelGodOfwar/RotationalWrench/raw/main/versioncheck/versions.xml";
	public boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public static boolean UpdateCheck;
	public String DownloadLink = "https://dev.bukkit.org/projects/rotationalwrench/files";
	/** end update checker variables */
	Version MINIMUM_MINECRAFT_VERSION = new Version("1.20");
	Version MAXIMUM_MINECRAFT_VERSION = new Version("26.3");
	Version CURRENT_MINECRAFT_VERSION = Version.getCurrentVersion();
	public boolean debug = false;
	public static String daLang;
	public YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static String datafolder;
	public boolean colorful_console;
	//public final ItemStack oldWrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
	public final NamespacedKey RECIPE_KEY = new NamespacedKey(this, "rotational_wrench");
	@SuppressWarnings("unused")
	private final List<BlockFace> faces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);
	/**private final List<BlockFace> skullfaces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST
    		, BlockFace.NORTH_EAST, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST
    		, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST);*/
	//public final ItemStack wrench = new ItemStack(Material.STICK, 1);
	public ShapedRecipe recipe;
	public ShapelessRecipe shapelessRecipe;
	public String pluginName = THIS_NAME;
	public Translator lang2;
	//private Set<String> triggeredPlayers = new HashSet<>();
	public String jarfilename = this.getFile().getAbsoluteFile().toString();
	public DetailedErrorReporter reporter;
	public PluginLogger LOGGER;
	public CoreUtils coreUtils = new CoreUtils(this);
	public JsonMessageUtils jsonMessageUtils;
	public ItemStack wrench;
	public RecipeManager recipeManager;

	@SuppressWarnings("unused") @Override // TODO: onEnable
	public void onEnable(){
		long startTime = System.currentTimeMillis();
		reporter = new DetailedErrorReporter(this);
		UpdateCheck = getConfig().getBoolean("auto_update_check", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		oldconfig = new YamlConfiguration();
		datafolder = this.getDataFolder().toString();
		colorful_console = getConfig().getBoolean("colorful_console", true);
		lang2 = new Translator(daLang, getDataFolder().toString());
		THIS_NAME = this.getDescription().getName();
		THIS_VERSION = this.getDescription().getVersion();
		if(!getConfig().getBoolean("console.longpluginname", true)) {
			pluginName = "RW";
		}else {
			pluginName = THIS_NAME;
		}
		this.LOGGER = new PluginLogger(pluginName, () -> debug);
		CoreUtils.initLogger(LOGGER);
		jsonMessageUtils = coreUtils.jsonMessageUtils;

		LOGGER.log(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
		LOGGER.log(ChatColor.GREEN + " v" + THIS_VERSION + ChatColor.RESET + " Loading...");
		LOGGER.log("Server Version: " + getServer().getVersion());

		Version checkVersion = this.verifyMinecraftVersion();

		/* DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			LOGGER.log("YOU ARE USING A DEV=BUILD, PLEASE REPORT ANY ISSUES.");
		}

		/*	Check for config */
		try{
			if(!getDataFolder().exists()){
				LOGGER.log("Data Folder doesn't exist");
				LOGGER.log("Creating Data Folder");
				getDataFolder().mkdirs();
				LOGGER.log("Data Folder Created at " + getDataFolder());
			}
			File file = new File(getDataFolder(), "config.yml");
			LOGGER.debug("" + file);
			if(!file.exists()){
				LOGGER.log("config.yml not found, creating!");
				saveResource("config.yml", true);
			}
			file = new File(getDataFolder(), "config_comments.yml");
			LOGGER.debug("" + file);
			if(!file.exists()){
				LOGGER.log("config_comments.yml not found, creating!");
				saveResource("config_comments.yml", true);
			}
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}

		/* Check if config.yml is up to date.*/
		boolean needConfigUpdate = false;
		//String oldConfig = new File(getDataFolder(), "config.yml").getPath().toString();
		try {
			oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
		} catch (Exception exception) {
			LOGGER.warn("Could not load config.yml");
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
		}
		String checkConfigversion = oldconfig.getString("version", "1.0.0");
		if(checkConfigversion != null){
			if(!checkConfigversion.equalsIgnoreCase("1.0.19")){
				needConfigUpdate = true;
			}
		}
		if(needConfigUpdate){
			try {
				FileStuff.copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
			}
			saveResource("config.yml", true);
			saveResource("config_comments.yml", true);
			try {
				config.load(new File(getDataFolder(), "config.yml"));
			} catch (Exception exception) {
				LOGGER.warn("Could not load config.yml");
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
			}
			try {
				oldconfig.load(new File(getDataFolder(), "old_config.yml"));
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
			}
			config.set("auto_update_check", oldconfig.get("auto_update_check", true));
			config.set("debug", oldconfig.get("debug", false));
			config.set("lang", oldconfig.get("lang", "en_US"));
			config.set("colorful_console", oldconfig.get("colorful_console", true));
			config.set("blacklist", oldconfig.get("blacklist", ""));
			config.set("spamfilter", oldconfig.get("spamfilter", true));

			config.set("auto_apply_resource_pack", oldconfig.get("auto_apply_resource_pack", false));

			config.set("enabled.anvil", oldconfig.get("enabled.anvil", true));
			config.set("enabled.armorstands", oldconfig.get("enabled.armorstands", true));
			config.set("enabled.banner", oldconfig.get("enabled.banner", true));
			config.set("enabled.basalt", oldconfig.get("enabled.basalt", true));
			config.set("enabled.beds", oldconfig.get("enabled.beds", true));
			config.set("enabled.bee", oldconfig.get("enabled.bee", true));
			config.set("enabled.bell", oldconfig.get("enabled.bell", true));
			config.set("enabled.bone", oldconfig.get("enabled.bone", true));
			config.set("enabled.buttons", oldconfig.get("enabled.buttons", true));
			config.set("enabled.campfires", oldconfig.get("enabled.campfires", true));
			config.set("enabled.carvedpumpkin", oldconfig.get("enabled.carvedpumpkin", true));
			config.set("enabled.chain", oldconfig.get("enabled.chain", true));
			config.set("enabled.chests", oldconfig.get("enabled.chests", true));
			config.set("enabled.coral", oldconfig.get("enabled.coral", true));
			config.set("enabled.doors", oldconfig.get("enabled.doors", true));
			config.set("enabled.endrod", oldconfig.get("enabled.endrod", true));
			config.set("enabled.fencegates", oldconfig.get("enabled.fencegates", true));
			config.set("enabled.heads", oldconfig.get("enabled.heads", true));
			config.set("enabled.ladder", oldconfig.get("enabled.ladder", true));
			config.set("enabled.lantern", oldconfig.get("enabled.lantern", true));
			config.set("enabled.lightning_rod", oldconfig.get("enabled.lightning_rod", true));
			config.set("enabled.logs", oldconfig.get("enabled.logs", true));
			config.set("enabled.pillars", oldconfig.get("enabled.pillars", true));
			config.set("enabled.rails", oldconfig.get("enabled.rails", true));
			config.set("enabled.redstone", oldconfig.get("enabled.redstone", true));
			config.set("enabled.shelfs", oldconfig.get("enabled.shelfs", true));
			config.set("enabled.shulker", oldconfig.get("enabled.shulker", true));
			config.set("enabled.signs", oldconfig.get("enabled.signs", true));
			config.set("enabled.slabs", oldconfig.get("enabled.slabs", true));
			config.set("enabled.stairs.rotate", oldconfig.get("enabled.stairs.rotate", true));
			config.set("enabled.stairs.invert", oldconfig.get("enabled.stairs.invert", true));
			config.set("enabled.terracotta", oldconfig.get("enabled.terracotta", true));
			config.set("enabled.torch", oldconfig.get("enabled.torch", true));
			config.set("enabled.trapdoors.rotate", oldconfig.get("enabled.trapdoors.rotate", true));
			config.set("enabled.trapdoors.invert", oldconfig.get("enabled.trapdoors.invert", true));
			config.set("enabled.walls", oldconfig.get("enabled.walls", true));
			config.set("enabled.workstations", oldconfig.get("enabled.workstations", true));

			try {
				config.save(new File(getDataFolder(), "config.yml"));
				config.load(new File(getDataFolder(), "config.yml"));
			} catch (IOException | InvalidConfigurationException exception) {
				LOGGER.warn("Could not save old settings to config.yml");
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
			}
			LOGGER.log("config.yml has been updated");
		}
		/* End Config update check */

		recipeManager = new RecipeManager(this);
		recipeManager.loadRecipeConfig();
		createWrenchItem();
		recipeManager.loadRecipe();

		/* Update Checker */
		if(UpdateCheck){
			try {
				LOGGER.log("Checking for updates...");
				VersionChecker updater = new VersionChecker(this, projectID, githubURL);
				if(updater.checkForUpdates()) {
					/* Update available */
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();

					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					LOGGER.log("* " + get("rw.version.message").replace("<MyPlugin>", THIS_NAME) );
					LOGGER.log("* " + get("rw.version.old_vers") + ChatColor.RED + UColdVers );
					LOGGER.log("* " + get("rw.version.new_vers") + ChatColor.GREEN + UCnewVers );
					LOGGER.log("*");
					LOGGER.log("* " + get("rw.version.please_update") );
					LOGGER.log("*");
					LOGGER.log("* " + get("rw.version.download") + ": " + DownloadLink + "/history");
					LOGGER.log("* " + get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				}else{
					/* Up to date */
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					LOGGER.log("* " + get("rw.version.curvers"));
					LOGGER.log("* " + get("rw.version.donate") + ": https://ko-fi.com/joelgodofwar");
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					UpdateAvailable = false;
				}
			}catch(Exception exception) {
				/* Error */
				LOGGER.log(get("rw.version.update.error"));
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
			}
		}else {
			/* auto_update_check is false so nag. */
			LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
			LOGGER.log("* " + get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
			LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
		}
		/* end update checker */

		LOGGER.debug("CURRENT_MINECRAFT_VERSION=" + CURRENT_MINECRAFT_VERSION);

		if( CURRENT_MINECRAFT_VERSION.isAtLeast("26.1") ) {
			getServer().getPluginManager().registerEvents( new RW_26_R1(this), this);
		}else if( CURRENT_MINECRAFT_VERSION.isBetween("1.21.4", "1.21.11") ) {
			getServer().getPluginManager().registerEvents( new RW_1_21_R2(this), this);
		}else if( CURRENT_MINECRAFT_VERSION.isBetween("1.21", "1.21.3") ) {
			getServer().getPluginManager().registerEvents( new RW_1_21_R1(this), this);
		}else if( CURRENT_MINECRAFT_VERSION.isBetween("1.20", "1.20.6") ){
			getServer().getPluginManager().registerEvents( new RW_1_20_R1(this), this);
		}else{
			// Below 1.20 (or some unparseable version)
			CoreUtils.warn(ChatColor.RED + "This plugin requires Minecraft 1.20 or newer. Detected: " + CURRENT_MINECRAFT_VERSION + ChatColor.RESET);
			CoreUtils.warn(ChatColor.RED + "Plugin will not load." + ChatColor.RESET);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Register Commands
		RWCommandHandler commandHandler = new RWCommandHandler(this);
		Objects.requireNonNull(getCommand("RWrench"), "Command 'RWrench' not found in plugin.yml!")
				.setExecutor(commandHandler);
		Objects.requireNonNull(getCommand("RWrench"), "Command 'RWrench' not found in plugin.yml!")
				.setTabCompleter(commandHandler);

		// Register Events
		Bukkit.getPluginManager().registerEvents(new PlayerJoinEventHandler(this), this);
		Bukkit.getPluginManager().registerEvents(new CraftItemEventHandler(this), this);

		LOGGER.log("Events Registered.");

		consoleInfo("ENABLED - Loading took " + LoadTime(startTime));

		try {
			//PluginBase plugin = this;
			Metrics metrics  = new Metrics(this, 9190);
			// New chart here
			// myPlugins()
			metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> call() throws Exception {
					Map<String, Integer> valueMap = new HashMap<>();

					if(getServer().getPluginManager().getPlugin("DragonDropElytra") != null){valueMap.put("DragonDropElytra", 1);}
					if(getServer().getPluginManager().getPlugin("NoEndermanGrief") != null){valueMap.put("NoEndermanGrief", 1);}
					if(getServer().getPluginManager().getPlugin("PortalHelper") != null){valueMap.put("PortalHelper", 1);}
					if(getServer().getPluginManager().getPlugin("ShulkerRespawner") != null){valueMap.put("ShulkerRespawner", 1);}
					if(getServer().getPluginManager().getPlugin("MoreMobHeads") != null){valueMap.put("MoreMobHeads", 1);}
					if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){valueMap.put("SilenceMobs", 1);}
					if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
					if(getServer().getPluginManager().getPlugin("VillagerWorkstationHighlights") != null){valueMap.put("VillagerWorkstationHighlights", 1);}
					//if(getServer().getPluginManager().getPlugin("RotationalWrench") != null){valueMap.put("RotationalWrench", 1);}
					return valueMap;
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("auto_update_check", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("auto_update_check")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_debug", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("debug")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_lang", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("lang")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("colorful_console", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("colorful_console")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("redstone", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.redstone")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("terracotta", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.terracotta")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("stairs_rotate", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.stairs.rotate")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("stairs_invert", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.stairs.invert")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("slabs", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.slabs")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("armorstands", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.armorstands")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("rails", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.rails")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("beds", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.beds")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("chain", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.chain")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("trapdoors_rotate", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.trapdoors.rotate")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("trapdoors_invert", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.trapdoors.invert")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("carvedpumpkin", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.carvedpumpkin")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("chests", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.chests")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("fencegates", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.fencegates")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("doors", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.doors")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("workstations", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.workstations")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("endrod", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.endrod")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("logs", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.logs")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("heads", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.heads")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("bell", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enabled.bell")).toUpperCase();
				}
			}));
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_METRICS_LOAD_ERROR).error(exception));
		}
	}

	@Override // TODO: onDisable
	public void onDisable(){
		/* Experimental Code */
		Bukkit.removeRecipe(RECIPE_KEY);
		Bukkit.clearRecipes();
		/* Experimental Code */
		consoleInfo("DISABLED");
	}

	public void consoleInfo(String state) {
		//loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
		LOGGER.log(ChatColor.YELLOW + " v" + THIS_VERSION + ChatColor.RESET + " is " + state  + ChatColor.RESET);
		//loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
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

	public boolean isDebug(){
		return debug;
	}

	public boolean getBoolean(String string, boolean bool){
		return getConfig().getBoolean(string, bool);
	}

	public String LoadTime(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
		long milliseconds = elapsedTime % 1000;

		if (minutes > 0) {
			return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
		} else if (seconds > 0) {
			return String.format("%d s %d ms.", seconds, milliseconds);
		} else {
			return String.format("%d ms.", elapsedTime);
		}
	}

	@SuppressWarnings("static-access")
	public String get(String key, String... defaultValue) {
		return lang2.get(key, defaultValue);
	}

	public String getjarfilename() {
		return jarfilename;
	}

	public boolean getDebug() {
		return debug;
	}

	public void sendResourcePackDownloadLink(Player player) {
		String baseUrl = "https://raw.githubusercontent.com/JoelGodOfwar/RotationalWrench/main/resource/RWrench";
		Version ver = CURRENT_MINECRAFT_VERSION;

		String url;

		if (ver.isBetween("1.20", "1.20.1")) {
			url = baseUrl + "-15.zip";
		} else if (ver.isBetween("1.20.2", "1.20.3")) {
			url = baseUrl + "-18.zip";
		} else if (ver.equals(new Version("1.20.4"))) {
			url = baseUrl + "-22.zip";
		} else if (ver.isBetween("1.20.5", "1.20.6")) {
			url = baseUrl + "-32.zip";
		} else if (ver.isBetween("1.21", "1.21.1")) {
			url = baseUrl + "-34.zip";
		} else if (ver.isBetween("1.21.2", "1.21.3")) {
			url = baseUrl + "-42.zip";
		} else if (ver.equals(new Version("1.21.4"))) {
			url = baseUrl + "-46.zip";
		} else if (ver.equals(new Version("1.21.5"))) {
			url = baseUrl + "-55.zip";
		} else if (ver.equals(new Version("1.21.6"))) {
			url = baseUrl + "-63.zip";
		} else if (ver.isBetween("1.21.7", "1.21.8")) {
			url = baseUrl + "-64.zip";
		} else if (ver.isBetween("1.21.9", "1.21.10")) {
			url = baseUrl + "-69.zip";
		} else if (ver.equals(new Version("1.21.11"))) {
			url = baseUrl + "-75.zip";
		} else if (ver.isBetween("26.1", "26.1.2")) {
			url = baseUrl + "-84.zip";
		} else if (ver.equals(new Version("26.2"))) {
			url = baseUrl + "-88.zip";
		} else if (ver.isAtLeastButBelow("26.3", "26.4")) {
			url = baseUrl + "-97.1.zip";
		} else {
			url = baseUrl + "-97.1.zip";   // fallback
		}

		player.sendMessage("§7Please download and apply it manually: " + url);
	}

	public void setResourcePackTest(Player player) {
		String baseUrl = "https://raw.githubusercontent.com/JoelGodOfwar/RotationalWrench/main/resource/RWrench";
		String url = baseUrl + "-1.21.4+.zip";
		byte[] hash = parseHash("fbc37a695228de833bdd5be55f36b2b1fdf6b8f8");   // ← update after upload

		String prompt = "§6Rotational Wrench Resource Pack (Test)\n§7Required for custom wrench";
		LOGGER.debug("Sending resource pack to " + player.getName() + " | URL: " + url);
		player.setResourcePack(UUID.randomUUID(), url, hash, prompt, true);
	}

	public void setResourcePack(Player player) {
		String baseUrl = "https://raw.githubusercontent.com/JoelGodOfwar/RotationalWrench/main/resource/RWrench";

		String url = null;
		byte[] hash = null;

		Version ver = CURRENT_MINECRAFT_VERSION;

		// ==================== VERSION SPECIFIC PACKS ====================
		if (ver.isBetween("1.20.3", "1.20.3")) {
			url = baseUrl + "-18.zip";
			hash = parseHash("87da298234682ca1ca9688da7cba7f3ff5489344");
		} else if (ver.equals(new Version("1.20.4"))) {
			url = baseUrl + "-22.zip";
			hash = parseHash("336a329ca35debd79056c40dda1f455f735df99e");

		} else if (ver.isBetween("1.20.5", "1.20.6")) {
			url = baseUrl + "-32.zip";
			hash = parseHash("c056c4428b8515f4dfaf2cc3ef32a268539622d9");

		} else if (ver.isBetween("1.21", "1.21.1")) {
			url = baseUrl + "-34.zip";
			hash = parseHash("d4a6e125a6c71a5ddbbbc5606c2b7fad723da252");

		} else if (ver.isBetween("1.21.2", "1.21.3")) {
			url = baseUrl + "-42.zip";
			hash = parseHash("4748e7aa84c67dad946c760787cfe2bd396ef657");

		} else if (ver.equals(new Version("1.21.4"))) {
			url = baseUrl + "-46.zip";
			hash = parseHash("5b959659ef6c022b677b816487b6b510e9e17e11");

		} else if (ver.equals(new Version("1.21.5"))) {
			url = baseUrl + "-55.zip";
			hash = parseHash("1d41725822502a043e3f67b1135d2d05d8596e3d");

		} else if (ver.equals(new Version("1.21.6"))) {
			url = baseUrl + "-63.zip";
			hash = parseHash("4434a1d8669cf2a4d6a7968e934662e8d9205282");

		} else if (ver.isBetween("1.21.7","1.21.8")) {
			url = baseUrl + "-64.zip";
			hash = parseHash("2e1ef30e6c3e4fd99130f90238693d434d14d361");

		} else if (ver.isBetween("1.21.9","1.21.10")) {
			url = baseUrl + "-69.zip";
			hash = parseHash("144158407fc19622a3310f56da1ece23b2ee6155");

		} else if (ver.isBetween("1.21.11","1.21.11")) {
			url = baseUrl + "-75.zip";
			hash = parseHash("3925cf2ec120fe858e2eee9ca7b6fb2ab7d8fc89");

		} else if (ver.isAtLeast("26.1")) {
			url = baseUrl + "-84.zip";
			hash = parseHash("805b2fd0b217a7310c977d5aafab6d655c4c032e");
		}

		if (url == null || hash == null) {
			player.sendMessage("§cNo resource pack available for your version.");
			return;
		}

		String prompt = "§6Rotational Wrench Resource Pack\n§7Required for custom wrench";

		// Modern method (1.20.3+ including 1.21 and 26.1)
		player.setResourcePack(UUID.randomUUID(), url, hash, prompt, true);
	}

	private byte[] parseHash(String hex) {
		if (hex == null || hex.isEmpty()) return null;

		byte[] bytes = new BigInteger(hex, 16).toByteArray();

		// Remove the extra leading zero byte that BigInteger sometimes adds
		if (bytes.length == 21 && bytes[0] == 0) {
			bytes = Arrays.copyOfRange(bytes, 1, 21);
		}
		return bytes;
	}

	public static RotationalWrench getInstance() {
		return getPlugin(RotationalWrench.class);
	}

	// Used to check the Minecraft version
	private Version verifyMinecraftVersion() {
		try {
			// We'll just warn the user for now
			if (CURRENT_MINECRAFT_VERSION.compareTo(MINIMUM_MINECRAFT_VERSION) < 0) {
				LOGGER.warn("Version " + CURRENT_MINECRAFT_VERSION + " is lower than the minimum " + MINIMUM_MINECRAFT_VERSION);
			}
			if (CURRENT_MINECRAFT_VERSION.compareTo(MAXIMUM_MINECRAFT_VERSION) > 0) {
				LOGGER.warn("Version " + CURRENT_MINECRAFT_VERSION + " has not yet been tested! Proceed with caution.");
			}
			return CURRENT_MINECRAFT_VERSION;
		} catch (Exception exception) {
			reporter.reportWarning(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_PARSE_MINECRAFT_VERSION).error(exception).messageParam(MAXIMUM_MINECRAFT_VERSION));
			// Unknown version - just assume it is the latest
			return MAXIMUM_MINECRAFT_VERSION;
		}
	}

	private void createWrenchItem() {
		wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
		ItemMeta meta = Objects.requireNonNull(wrench.getItemMeta());

		meta.setDisplayName(ChatColor.RESET + recipeManager.recipeConfig.getString("translation", "Rotational Wrench"));
		meta.setUnbreakable(recipeManager.recipeConfig.getBoolean("unbreakable", true));

		// Version-specific CustomModelData
		Version ver = Version.getCurrentVersion();

		if (ver.atOrAbove(new Version("1.21.4"))) {
			// New system (1.21.4+)
			CustomModelDataComponent cmdc = meta.getCustomModelDataComponent();
			cmdc.setStrings(List.of("rwrench"));
			meta.setCustomModelDataComponent(cmdc);
		} else {
			// Old system
			meta.setCustomModelData(4321);
		}

		wrench.setItemMeta(meta);
	}


}
