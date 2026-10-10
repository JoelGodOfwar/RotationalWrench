package com.github.joelgodofwar.rw.commands;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.enums.Perms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class RWCommandHandler implements CommandExecutor, TabCompleter {

    private final ReloadCommand reloadCommand;
    private final TextureCommand textureCommand;
    private final ToggleDebugCommand toggleDebugCommand;
    private final UpdateCommand updateCommand;   // we'll do this one next

    public RWCommandHandler(RotationalWrench plugin) {
        this.reloadCommand = new ReloadCommand(plugin);
        this.textureCommand = new TextureCommand(plugin);
        this.toggleDebugCommand = new ToggleDebugCommand(plugin);
        this.updateCommand = new UpdateCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("RWrench")) return false;

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "reload" -> reloadCommand.execute(sender, args);
            case "texture" -> textureCommand.execute(sender, args);
            case "update" -> updateCommand.execute(sender, args);
            case "toggledebug", "td" -> toggleDebugCommand.execute(sender, args);
            default -> {
                showHelp(sender);
                yield true;
            }
        };
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6=== Rotational Wrench Commands ===");
        sender.sendMessage("§e/rwrench texture §7- Get resource pack download link");
        if ( Perms.RELOAD.hasPermissionOrOp(sender) ) {
            sender.sendMessage("§e/rwrench reload §7- Reload the plugin");
        }
        if ( Perms.SHOW_UPDATE_AVAILABLE.hasPermissionOrOp(sender) ) {
            sender.sendMessage("§e/rwrench update §7- Check for updates");
        }
        if ( Perms.TOGGLE_DEBUG.hasPermissionOrOp(sender) ) {
            sender.sendMessage("§e/rwrench toggledebug / td §7- Toggle debug mode");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("texture");
            if (Perms.RELOAD.hasPermissionOrOp(sender)) {
                completions.add("reload");
            }
            if (Perms.SHOW_UPDATE_AVAILABLE.hasPermissionOrOp(sender)) {
                completions.add("update");
            }
            if ( Perms.TOGGLE_DEBUG.hasPermissionOrOp(sender) ) {
                completions.add("toggledebug");
                completions.add("td");
            }
        }

        // You can add deeper tab completion later (e.g. for specific arguments)
        return completions;
    }
}