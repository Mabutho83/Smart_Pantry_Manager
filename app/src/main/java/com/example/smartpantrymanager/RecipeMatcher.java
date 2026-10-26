package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.PantryItem;
import model.Recipe;
import model.RecipeIngredient;

/**
 * Core business logic: the strict-matching rule.
 * A recipe is only suggested if EVERY ingredient it needs is in the pantry
 * in at least the required quantity.In future this could be improved to
 * use some form of machine learning algo to
 */
public class RecipeMatcher {

    /** Returns only the recipes that can be made right now. */
    public static List<Recipe> findStrictMatches(List<PantryItem> pantry, List<Recipe> recipes) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantry)) {
                result.add(recipe);
            }
        }
        return result;
    }

    /** True only if every required ingredient is available. One missing ingredient = false. */
    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        if (recipe.getIngredients().isEmpty()) return false;
        for (RecipeIngredient needed : recipe.getIngredients()) {
            if (!hasEnough(needed, pantry)) return false;
        }
        return true;
    }

    private static boolean hasEnough(RecipeIngredient needed, List<PantryItem> pantry) {
        String key = normalise(needed.getName());
        int neededCategory = Units.category(needed.getUnit());

        boolean found = false;       // pantry has this ingredient by name
        boolean comparable = false;  // at least one pantry entry uses a compatible unit
        double have = 0;

        for (PantryItem item : pantry) {
            if (!normalise(item.getName()).equals(key)) continue;
            found = true;
            if (Units.category(item.getUnit()) == neededCategory) {
                comparable = true;
                have += Units.toBase(item.getQuantity(), item.getUnit()); // adds up duplicate entries
            }
        }

        if (!found) return false;
        // e.g. pantry has salt in grams but recipe wants teaspoons: can't compare, so presence counts.
        if (!comparable) return true;
        return have + 1e-9 >= Units.toBase(needed.getQuantity(), needed.getUnit());
    }

    /** Lower-cases, trims and crudely singularizes so "Tomatoes" and "tomato" match. */
    public static String normalise(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (n.endsWith("ies") && n.length() > 4) return n.substring(0, n.length() - 3) + "y";
        if (n.endsWith("oes")) return n.substring(0, n.length() - 2);
        if (n.endsWith("ches") || n.endsWith("shes") || n.endsWith("sses") || n.endsWith("xes"))
            return n.substring(0, n.length() - 2);
        if (n.endsWith("ss")) return n;
        if (n.endsWith("s") && n.length() > 3) return n.substring(0, n.length() - 1);
        return n;
    }
}
