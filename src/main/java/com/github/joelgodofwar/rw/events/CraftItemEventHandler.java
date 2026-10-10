package com.github.joelgodofwar.rw.events;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.util.StrUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Recipe;

import java.util.Objects;

public class CraftItemEventHandler implements Listener {

    private final RotationalWrench plugin;

    public CraftItemEventHandler(RotationalWrench plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();

        // Check if player is crafting the wrench
        if (recipe != null && recipe.equals(plugin.wrench)) {
            // Check blacklist
            boolean onBlacklist = StrUtils.stringContains(
                    plugin.getConfig().getString("blacklist", ""),
                    Objects.requireNonNull(Objects.requireNonNull(event.getInventory().getLocation()).getWorld()).getName()
            );

            if (onBlacklist) {
                event.setCancelled(true);
                return;
            }

            Player player = (Player) event.getWhoClicked();

            // Auto Apply Resource Pack when crafting wrench
            if (plugin.getConfig().getBoolean("auto_apply_resource_pack", false)) {
                plugin.setResourcePack(player);
            }
        }
    }
}