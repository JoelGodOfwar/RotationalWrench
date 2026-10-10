package com.github.joelgodofwar.rw.commands;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.enums.Perms;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ToggleDebugCommand {

    private final RotationalWrench plugin;

    public ToggleDebugCommand(RotationalWrench plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!Perms.TOGGLE_DEBUG.hasPermission(sender)) {
            sender.sendMessage(ChatColor.YELLOW + plugin.pluginName + ChatColor.RED + " " + plugin.get("rw.message.noperm"));
            return true;
        }

        plugin.debug = !plugin.debug;

        String message = plugin.get("rw.message.debugtrue")
                .replace("<boolean>", plugin.get("rw.message.boolean." + String.valueOf(plugin.debug).toLowerCase()));

        sender.sendMessage(ChatColor.YELLOW + plugin.pluginName + ChatColor.RESET + message);

        return true;
    }
}