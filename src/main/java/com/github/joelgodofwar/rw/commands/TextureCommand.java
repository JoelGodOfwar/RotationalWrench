package com.github.joelgodofwar.rw.commands;

import com.github.joelgodofwar.rw.RotationalWrench;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TextureCommand {

    private final RotationalWrench plugin;

    public TextureCommand(RotationalWrench plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        plugin.sendResourcePackDownloadLink(player);
        return true;
    }
}