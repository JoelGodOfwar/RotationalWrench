package com.github.joelgodofwar.rw.util;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StringSetTag {

	private final Set<String> materials;

	public StringSetTag(String... materials) {
		this.materials = Sets.newHashSet(Lists.newArrayList(materials));
	}

	public boolean isTagged(@Nonnull String item) {
		return this.materials.contains(item);
	}

	public @Nonnull Set<String> getValues() {
		return this.materials;
	}
}