package com.github.joelgodofwar.rw.commands;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.common.PluginLibrary;
import com.github.joelgodofwar.rw.common.error.Report;
import com.github.joelgodofwar.rw.enums.Perms;
import lib.github.joelgodofwar.coreutils.util.VersionChecker;

import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class UpdateCommand {

    private final RotationalWrench plugin;
    public PluginLogger LOGGER;

    public UpdateCommand(RotationalWrench plugin) {
        this.plugin = plugin;
        this.LOGGER = plugin.LOGGER;
    }

    public boolean execute(CommandSender sender, String[] args) {

        // Console handling
        if (!(sender instanceof Player player)) {
            runUpdateCheckConsole();
            return true;
        }

        // Player handling

        if (!Perms.SHOW_UPDATE_AVAILABLE.hasPermissionOrOp(player)) {
            player.sendMessage(ChatColor.YELLOW + RotationalWrench.THIS_NAME + " "
                    + plugin.get("rw.message.noperm").replace("<perm>", "rotationalwrench.showUpdateAvailable"));
            return true;
        }

        // Run update check on main thread with slight delay for player
        BukkitTask task = Bukkit.getScheduler().runTask(plugin, () -> runUpdateCheckPlayer(player));
        return true;
    }

    private void runUpdateCheckConsole() {
        try {
            Bukkit.getConsoleSender().sendMessage("Checking for updates...");
            VersionChecker updater = new VersionChecker(plugin, plugin.projectID, plugin.githubURL);

            if (updater.checkForUpdates()) {
                plugin.UpdateAvailable = true;
                plugin.UColdVers = updater.oldVersion();
                plugin.UCnewVers = updater.newVersion();

                LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                LOGGER.log("* " + plugin.get("rw.version.message").replace("<MyPlugin>", RotationalWrench.THIS_NAME));
                LOGGER.log("* " + plugin.get("rw.version.old_vers") + ChatColor.RED + plugin.UColdVers);
                LOGGER.log("* " + plugin.get("rw.version.new_vers") + ChatColor.GREEN + plugin.UCnewVers);
                LOGGER.log("* " + plugin.get("rw.version.please_update"));
                LOGGER.log("* " + plugin.get("rw.version.download") + ": " + plugin.DownloadLink + "/history");
                LOGGER.log("* " + plugin.get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
                LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
            } else {
                plugin.UpdateAvailable = false;
                LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                LOGGER.log("* " + ChatColor.YELLOW + RotationalWrench.THIS_NAME + ChatColor.RESET + " " + plugin.get("rw.version.curvers"));
                LOGGER.log("* " + plugin.get("rw.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
                LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + plugin.get("rw.version.update.error"));
            plugin.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(e));
        }
    }

    private void runUpdateCheckPlayer(Player player) {
        try {
            Bukkit.getConsoleSender().sendMessage("Checking for updates...");
            VersionChecker updater = new VersionChecker(plugin, plugin.projectID, plugin.githubURL);

            if (updater.checkForUpdates()) {
                plugin.UpdateAvailable = true;
                plugin.UColdVers = updater.oldVersion();
                plugin.UCnewVers = updater.newVersion();

                String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>.\"}}]";

                links = links.replace("<DownloadLink>", plugin.DownloadLink)
                        .replace("<Download>", plugin.get("rw.version.download"))
                        .replace("<Donate>", plugin.get("rw.version.donate"))
                        .replace("<please_update>", plugin.get("rw.version.please_update"))
                        .replace("<Donate_msg>", plugin.get("rw.version.donate.message"))
                        .replace("<Notes>", plugin.get("rw.version.notes"))
                        .replace("<Notes_msg>", plugin.get("rw.version.notes.message"));

                String versions = ChatColor.GRAY + plugin.get("rw.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | "
                        + plugin.get("rw.version.old_vers") + ": " + ChatColor.RED + "{oVers}";

                player.sendMessage(ChatColor.GRAY + plugin.get("rw.version.message")
                        .replace("<MyPlugin>", ChatColor.GOLD + RotationalWrench.THIS_NAME + ChatColor.GRAY));

                plugin.jsonMessageUtils.sendJsonMessage(player, links);
                player.sendMessage(versions.replace("{nVers}", plugin.UCnewVers).replace("{oVers}", plugin.UColdVers));

            } else {
                String links = "{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}}";
                links = links.replace("<Donate>", plugin.get("rw.version.donate"))
                        .replace("<Donate_msg>", plugin.get("rw.version.donate.message"));

                plugin.jsonMessageUtils.sendJsonMessage(player, links);
                player.sendMessage(ChatColor.YELLOW + RotationalWrench.THIS_NAME + ChatColor.RED + " v" + RotationalWrench.THIS_VERSION
                        + ChatColor.RESET + " " + plugin.get("rw.version.curvers"));

                plugin.UpdateAvailable = false;
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + plugin.get("rw.version.update.error"));
            plugin.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(e));
        }
    }
}