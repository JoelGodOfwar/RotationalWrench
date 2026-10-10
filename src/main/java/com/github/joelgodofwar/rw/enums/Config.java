package com.github.joelgodofwar.rw.enums;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import java.util.stream.Collectors;

public enum Config {
    DEBUG("debug", true),
    USE("use", "default"),
    ADMIN_GROUP("admin-group", List.of("op", "admin"));
    // Add more as needed, e.g., MAX_WRENCH_USES("max-wrench-uses", 5)

    private static FileConfiguration config; // Set via static setter, e.g., Config.setConfig(yourConfig);

    private static void setConfig(FileConfiguration cfg) {
        config = cfg;
    }

    private final String key;
    private final Object defaultValue;

    Config(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the config key/path (e.g., "debug").
     * @return the config key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns a string representation of the config value (e.g., "true", "default", "[op, admin]").
     * Falls back to default if config not set.
     * @return the value as a string.
     */
    @Override
    public String toString() {
        Object value = get();
        if (value instanceof List<?> list) {
            return "[" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
        }
        return value != null ? value.toString().toUpperCase() : "null"; // Uppercase for booleans/ints for visibility
    }

    /**
     * Fetches the value from the config, falling back to the default.
     * @return the config value as Object; use typed getters for safety.
     */
    public Object get() {
        if (config == null) {
            return defaultValue;
        }
        return config.get(key, defaultValue); // Generic fallback; typed methods handle specifics
    }

    /**
     * Convenience method for boolean configs; throws if not applicable.
     * Usage: Config.DEBUG.getBoolean()
     */
    public boolean getBoolean() {
        if (!(defaultValue instanceof Boolean)) {
            throw new IllegalStateException("Config." + name() + " is not a boolean type");
        }
        if (config == null) {
            return (Boolean) defaultValue;
        }
        return config.getBoolean(key, (Boolean) defaultValue);
    }

    /**
     * Convenience method for string configs; throws if not applicable.
     * Usage: Config.USE.getString()
     */
    public String getString() {
        if (!(defaultValue instanceof String)) {
            throw new IllegalStateException("Config." + name() + " is not a string type");
        }
        if (config == null) {
            return (String) defaultValue;
        }
        return config.getString(key, (String) defaultValue);
    }

    /**
     * Convenience method for list configs (assumes List<String>); throws if not applicable.
     * Usage: Config.ADMIN_GROUP.getList()
     */
    @SuppressWarnings("unchecked")
    public List<String> getList() {
        if (!(defaultValue instanceof List<?>)) {
            throw new IllegalStateException("Config." + name() + " is not a list type");
        }
        if (config == null) {
            return (List<String>) defaultValue;
        }
        List<String> value = config.getStringList(key);
        return value.isEmpty() ? (List<String>) defaultValue : value;
    }

    /**
     * Convenience method for integer configs; throws if not applicable.
     * Usage: Config.MAX_WRENCH_USES.getInt()
     */
    public int getInt() {
        if (!(defaultValue instanceof Integer)) {
            throw new IllegalStateException("Config." + name() + " is not an int type");
        }
        if (config == null) return (Integer) defaultValue;
        return config.getInt(key, (Integer) defaultValue);
    }

    }