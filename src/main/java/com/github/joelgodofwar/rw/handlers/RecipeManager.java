package com.github.joelgodofwar.rw.handlers;

import com.github.joelgodofwar.rw.RotationalWrench;
import com.github.joelgodofwar.rw.common.PluginLibrary;
import com.github.joelgodofwar.rw.common.error.Report;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeManager {

    private final RotationalWrench RW;
    public YamlConfiguration recipeConfig = new YamlConfiguration();

    public RecipeManager(RotationalWrench plugin) {
        this.RW = plugin;
    }

    public void loadRecipeConfig() {
        File file = new File(RW.getDataFolder(), "wrench_recipe.yml");

        if (!file.exists()) {
            RW.saveResource("wrench_recipe.yml", false);
            RW.LOGGER.log("Created default wrench_recipe.yml");
        }

        // Raw formatting fix first (tabs → spaces, etc.)
        boolean rawFixed = fixRawYamlFile(file);
        if (rawFixed) {
            RW.LOGGER.log("Fixed formatting issues in wrench_recipe.yml");
        }

        // Load the file (after raw fix)
        recipeConfig = YamlConfiguration.loadConfiguration(file);

        // Structure/logical fixes
        boolean structureFixed = autoFixRecipeStructure();
        if (structureFixed) {
            try {
                recipeConfig.save(file);
                RW.LOGGER.log("wrench_recipe.yml structure was corrected and saved.");
            } catch (Exception e) {
                RW.LOGGER.warn("Failed to save fixed wrench_recipe.yml: " + e.getMessage());
            }
        }
    }
    private boolean fixRawYamlFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            boolean changed = false;

            for (int i = 0; i < lines.size(); i++) {
                String original = lines.get(i);
                String fixed = original;

                // Replace tabs with 2 spaces
                fixed = fixed.replace("\t", "  ");

                // Trim trailing spaces
                fixed = fixed.replaceAll("\\s+$", "");

                if (!fixed.equals(original)) {
                    lines.set(i, fixed);
                    changed = true;
                }
            }

            if (changed) {
                Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            }
            return changed;
        } catch (Exception e) {
            RW.LOGGER.warn("Failed to fix raw wrench_recipe.yml: " + e.getMessage());
            return false;
        }
    }
    private boolean autoFixRecipeStructure() {
        boolean fixed = false;

        if (!recipeConfig.contains("type")) {
            recipeConfig.set("type", "SHAPED");
            fixed = true;
        }

        String type = recipeConfig.getString("type", "SHAPED").trim().toUpperCase();

        if (type.equals("SHAPED")) {
            if (!recipeConfig.isList("shaped.shape") || recipeConfig.getStringList("shaped.shape").size() != 3) {
                recipeConfig.set("shaped.shape", Arrays.asList(" g ", " gg", "i  "));
                fixed = true;
            }
            if (!recipeConfig.isList("shaped.ingredients") || recipeConfig.getStringList("shaped.ingredients").isEmpty()) {
                recipeConfig.set("shaped.ingredients", Arrays.asList("g:GOLD_INGOT", "i:IRON_INGOT"));
                fixed = true;
            }
        } else if (type.equals("SHAPELESS")) {
            if (!recipeConfig.isList("shapeless.ingredients") || recipeConfig.getStringList("shapeless.ingredients").isEmpty()) {
                recipeConfig.set("shapeless.ingredients", Arrays.asList("GOLD_NUGGET", "IRON_NUGGET", "CARROT_ON_A_STICK"));
                fixed = true;
            }
        }

        return fixed;
    }

    public void loadRecipe() {
        try {
            String type = recipeConfig.getString("type", "SHAPED").trim().toUpperCase();

            RW.recipe = null;

            if (type.equals("SHAPELESS")) {
                loadShapelessRecipe();
            } else {
                loadShapedRecipe();
            }

            // === VERIFICATION ===
            Recipe registered = Bukkit.getRecipe(RW.RECIPE_KEY);
            if (registered != null) {
                RW.LOGGER.log("✅ Recipe verified successfully! Key: " + RW.RECIPE_KEY + " (Type: " + type + ")");

                ItemStack result = registered.getResult();
                RW.LOGGER.log("   → Result: " + result.getType() + " x" + result.getAmount());

                if (registered instanceof ShapedRecipe shaped) {
                    RW.LOGGER.log("   → Shape: " + Arrays.toString(shaped.getShape()));
                    StringBuilder ing = new StringBuilder("   → Ingredients: ");
                    shaped.getIngredientMap().forEach((k, v) -> {
                        if (v != null) ing.append(k).append("=").append(v.getType()).append(" ");
                    });
                    RW.LOGGER.log(ing.toString());
                } else if (registered instanceof ShapelessRecipe shapeless) {
                    RW.LOGGER.log("   → Shapeless Ingredients: " + shapeless.getIngredientList().stream()
                            .map(is -> is.getType().toString())
                            .collect(Collectors.joining(", ")));
                }
            } else {
                RW.LOGGER.warn("❌ Recipe NOT found after registration! Key = " + RW.RECIPE_KEY);
                Bukkit.removeRecipe(RW.RECIPE_KEY);
            }

        } catch (Exception exception) {
            RW.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_RECIPES).error(exception));
        }
    }
    private void loadShapedRecipe() {
        List<String> defaultShape = Arrays.asList(" g ", " gg", "i  ");
        List<String> defaultIngredients = Arrays.asList("g:GOLD_INGOT", "i:IRON_INGOT");

        List<String> shape = recipeConfig.getStringList("shaped.shape");
        List<String> ingredients = recipeConfig.getStringList("shaped.ingredients");

        boolean validShape = shape != null && shape.size() == 3;
        boolean validIngredients = ingredients != null && !ingredients.isEmpty() && ingredients.size() <= 9;

        int nonSpaceCount = 0;
        if (validShape) {
            String shapeString = String.join("", shape);
            nonSpaceCount = shapeString.replace(" ", "").length();
        }

        boolean validCount = nonSpaceCount >= 1 && nonSpaceCount <= 9 &&
                ingredients != null && nonSpaceCount == ingredients.size();

        if (!validShape || !validIngredients || !validCount) {
            RW.LOGGER.warn("Invalid shaped recipe in wrench_recipe.yml. " +
                    "Shape must have 3 lines and number of non-space chars (" + nonSpaceCount +
                    ") must match ingredients count (1-9). Using default.");
            shape = defaultShape;
            ingredients = defaultIngredients;
        }

        RW.recipe = new ShapedRecipe(RW.RECIPE_KEY, RW.wrench);
        RW.recipe.shape(shape.get(0), shape.get(1), shape.get(2));

        for (String ing : ingredients) {
            String[] parts = ing.split(":", 2);
            if (parts.length == 2) {
                char key = parts[0].charAt(0);
                Material mat = Material.matchMaterial(parts[1]);
                if (mat != null) {
                    RW.recipe.setIngredient(key, mat);
                }
            }
        }

        registerRecipe(RW.recipe);
    }
    private void loadShapelessRecipe() {
        List<String> ingredients = recipeConfig.getStringList("shapeless.ingredients");

        if (ingredients.isEmpty()) {
            RW.LOGGER.warn("Invalid shapeless recipe. Using default.");
            ingredients = Arrays.asList("GOLD_NUGGET", "IRON_NUGGET", "CARROT_ON_A_STICK");
        }

        RW.shapelessRecipe = new ShapelessRecipe(RW.RECIPE_KEY, RW.wrench);

        for (String matName : ingredients) {
            Material mat = Material.matchMaterial(matName);
            if (mat != null) {
                RW.shapelessRecipe.addIngredient(mat);
            }
        }

        registerRecipe(RW.shapelessRecipe);
    }
    private void registerRecipe(Recipe r) {
        Bukkit.removeRecipe(RW.RECIPE_KEY);
        try {
            Bukkit.addRecipe(r);
            RW.LOGGER.log("Rotational Wrench Recipe added.");
        } catch (Exception e) {
            RW.LOGGER.log("Recipe already exists.");
        }
    }

    public void reload() {
        Bukkit.removeRecipe(RW.RECIPE_KEY);
        loadRecipeConfig();
        loadRecipe();
        RW.LOGGER.log("Recipe configuration reloaded and re-registered.");
    }
}