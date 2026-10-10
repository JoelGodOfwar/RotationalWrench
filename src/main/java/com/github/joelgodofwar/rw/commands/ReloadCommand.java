package com.github.joelgodofwar.rw.commands;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.common.PluginLibrary;
import com.github.joelgodofwar.rw.common.error.Report;
import com.github.joelgodofwar.rw.enums.Perms;
import com.github.joelgodofwar.rw.i18n.Translator;
import lib.github.joelgodofwar.coreutils.util.VersionChecker;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class ReloadCommand {

    private final RotationalWrench RW;
    public PluginLogger LOGGER;

    public ReloadCommand(RotationalWrench plugin) {
        this.RW = plugin;
        this.LOGGER = plugin.LOGGER;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!Perms.RELOAD.hasPermission(sender)) {
            sender.sendMessage("§cYou don't have permission to reload RotationalWrench.");
            return true;
        }

        long startTime = System.currentTimeMillis();

        LOGGER.log(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
        LOGGER.log(ChatColor.GREEN + " v" + RotationalWrench.THIS_VERSION + ChatColor.RESET + " Reloading...");
        LOGGER.log("Server Version: " + RW.getServer().getVersion());

        // === Reload Core Settings ===
        RW.reloadConfig();
        try {
            RW.config.load(new File(RW.getDataFolder(), "config.yml"));
        } catch (Exception exception) {
            LOGGER.warn("Could not load config.yml");
            RW.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
        }

        RotationalWrench.UpdateCheck = RW.getConfig().getBoolean("auto_update_check", true);
        RW.debug = RW.getConfig().getBoolean("debug", false);
        RotationalWrench.daLang = RW.getConfig().getString("lang", "en_US");
        RW.colorful_console = RW.getConfig().getBoolean("colorful_console", true);

        // Reload Translator
        RW.lang2 = new Translator(RotationalWrench.daLang, RW.getDataFolder().toString());

        // === Reload Recipe Config + Re-register Recipe ===
        try {
            RW.recipeManager.reload();

            LOGGER.log("Recipe configuration reloaded and re-registered.");
        } catch (Exception e) {
            LOGGER.warn("Failed to reload recipe: " + e.getMessage());
            RW.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_RECIPES).error(e));
        }

        /* Update Checker */
        if (RotationalWrench.UpdateCheck) {
            try {
                LOGGER.log("Checking for updates...");
                VersionChecker updater = new VersionChecker(RW, RW.projectID, RW.githubURL);

                if (updater.checkForUpdates()) {
                    RW.UpdateAvailable = true;
                    RW.UColdVers = updater.oldVersion();
                    RW.UCnewVers = updater.newVersion();

                    LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                    LOGGER.log("* " + RW.get("rw.version.message").replace("<MyPlugin>", RotationalWrench.THIS_NAME));
                    LOGGER.log("* " + RW.get("rw.version.old_vers") + ChatColor.RED + RW.UColdVers);
                    LOGGER.log("* " + RW.get("rw.version.new_vers") + ChatColor.GREEN + RW.UCnewVers);
                    LOGGER.log("* " + RW.get("rw.version.please_update"));
                    LOGGER.log("* " + RW.get("rw.version.download") + ": " + RW.DownloadLink + "/history");
                    LOGGER.log("* " + RW.get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
                    LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                } else {
                    RW.UpdateAvailable = false;
                    LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                    LOGGER.log("* " + RW.get("rw.version.curvers"));
                    LOGGER.log("* " + RW.get("rw.version.donate") + ": https://ko-fi.com/joelgodofwar");
                    LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                }
            } catch (Exception exception) {
                LOGGER.log(RW.get("rw.version.update.error"));
                RW.reporter.reportDetailed(RW, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
            }
        } else {
            /* auto_update_check is false */
            LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
            LOGGER.log("* " + RW.get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
            LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
        }

        RW.consoleInfo("§aRELOADED §7- Took " + RW.LoadTime(startTime));
        sender.sendMessage(ChatColor.YELLOW + RW.pluginName + ChatColor.RESET + RW.get("rw.message.reloaded"));

        return true;
    }
}