package com.github.joelgodofwar.rw.events;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.enums.Perms;
import com.github.joelgodofwar.rw.util.StrUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventHandler implements Listener {

    private final RotationalWrench plugin;

    public PlayerJoinEventHandler(RotationalWrench plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Recipe Discovery
        boolean onBlacklist = StrUtils.stringContains(
                plugin.getConfig().getString("blacklist", ""),
                player.getWorld().getName()
        );

        if (!onBlacklist) {
            player.discoverRecipe(plugin.RECIPE_KEY);
        }

        // Auto Apply Resource Pack
        if (plugin.getConfig().getBoolean("auto_apply_resource_pack", false)) {
            plugin.setResourcePack(player);
        }

        // Update Notification for Ops
        if (plugin.UpdateAvailable && (player.isOp() || Perms.SHOW_UPDATE_AVAILABLE.hasPermissionOrOp(player))) {
            String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";

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
            player.sendMessage(versions.replace("{nVers}", plugin.UCnewVers)
                    .replace("{oVers}", plugin.UColdVers));
        }

        // Special message for the developer
        if (player.getName().equals("JoelYahwehOfWar") || player.getName().equals("JoelGodOfWar")) {
            player.sendMessage(plugin.getName() + " " + plugin.getDescription().getVersion() + " Hello father!");
        }
    }
}