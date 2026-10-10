package com.github.joelgodofwar.rw.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Perms {
    SHOW_UPDATE_AVAILABLE("showUpdateAvailable"),
    USE("use"),
    ADMIN("admin"),
    RELOAD("reload"),
    TOGGLE_DEBUG("toggledebug");

    private static final String PREFIX = "rotationalwrench.";
    private final String permission;

    Perms(String permission) {
        this.permission = PREFIX + permission;
    }

    /**
     * Returns the full permission string (e.g., "rotationalwrench.use").
     * @return the permission string.
     */
    @Override
    public String toString() {
        return permission;
    }

    /**
     * Checks if a CommandSender has this specific permission.
     * Always returns true for the console.
     * @param sender The CommandSender to check.
     * @return true if the sender is the console or has the permission, false otherwise.
     */
    public boolean hasPermission(CommandSender sender) {
        return hasAdmin(sender) || !(sender instanceof Player) || sender.hasPermission(permission);
    }

    /**
     * Checks if a CommandSender has this permission or is an operator.
     * Always returns true for the console.
     * @param sender The CommandSender to check.
     * @return true if the sender is the console, has the permission, or is an op, false otherwise.
     */
    public boolean hasPermissionOrOp(CommandSender sender) {
        return hasAdmin(sender) || !(sender instanceof Player) || (sender.isOp() || sender.hasPermission(permission));
    }

    /**
     * Checks if a Player has this specific permission.
     * @param player The Player to check.
     * @return true if the player has the permission, false otherwise.
     */
    public boolean hasPermission(Player player) {
        return hasAdmin(player) || player.hasPermission(permission);
    }

    /**
     * Checks if a Player has this permission or is an operator.
     * @param player The Player to check.
     * @return true if the player has the permission or is an op, false otherwise.
     */
    public boolean hasPermissionOrOp(Player player) {
        return hasAdmin(player) || player.isOp() || player.hasPermission(permission);
    }

    private static boolean hasAdmin(CommandSender sender) {
        return sender.hasPermission(PREFIX + "admin");
    }
}