package com.github.joelgodofwar.rw.util;

import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MaterialSetTag implements Tag<Material> {

    private final NamespacedKey key;
    private final Set<Material> materials;

    public MaterialSetTag(NamespacedKey key, Material... materials) {
        this.key = key;
        this.materials = Sets.newEnumSet(Lists.newArrayList(materials), Material.class);
    }

    @Override
    public boolean isTagged(@Nonnull Material item) {
        return this.materials.contains(item);
    }

    @Override
    public @Nonnull Set<Material> getValues() {
        return this.materials;
    }

    @Override
    public @Nonnull NamespacedKey getKey() {
        return this.key;
    }
}