package com.github.joelgodofwar.rw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

public class WildcardStringSetTag {

	private final Set<String> exact = Sets.newHashSet();
	private final List<Pattern> patterns = new ArrayList<>();

	public WildcardStringSetTag(String... materials) {
		for (String m : materials) {
			if (m.indexOf('*') >= 0) {
				// simple glob → regex: * → .*
				String regex = m.replace("*", ".*");
				patterns.add(Pattern.compile(regex));
			} else {
				exact.add(m);
			}
		}
	}

	public boolean isTagged(@Nonnull String item) {
		if (exact.contains(item)) {
			return true;
		}
		for (Pattern p : patterns) {
			if (p.matcher(item).matches()) {
				return true;
			}
		}
		return false;
	}

	public @Nonnull Set<String> getValues() {
		return this.exact;
	}
}