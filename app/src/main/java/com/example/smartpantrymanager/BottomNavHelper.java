package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Wires up the bottom navigation bar shared by the Pantry, Recipes and Settings screens. */
public class BottomNavHelper {

    public static void setup(Activity activity, int selectedId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true; // already on this screen

            Intent intent;
            if (id == R.id.nav_pantry) {
                // Pantry is the home screen: return to it and discard screens above it.
                intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                Class<?> target = (id == R.id.nav_recipes)
                        ? SuggestedRecipesActivity.class
                        : SettingsActivity.class;
                intent = new Intent(activity, target);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);

            // Switching between Recipes and Settings replaces the current screen,
            // so pressing Back always returns to the Pantry.
            if (!(activity instanceof MainActivity) && id != R.id.nav_pantry) {
                activity.finish();
            }
            return true;
        });
    }
}
