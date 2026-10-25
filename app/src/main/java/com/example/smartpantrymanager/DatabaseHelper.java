package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import model.PantryItem;
import model.Recipe;
import model.RecipeIngredient;

/**
 * SQLite database: pantry items (full CRUD) and the seeded recipe collection.
 * Tables: pantry, recipes, recipe_ingredients (one recipe -> many ingredients).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Seed format:  name # step1|step2|... # ingredient:quantity:unit,ingredient:quantity:unit
    private static final String[] SEED_RECIPES = {
            "Tomato Pasta#Boil the pasta until tender.|Crush the tomatoes and fry with the garlic in olive oil.|Toss the pasta through the sauce and serve.#pasta:200:g,tomato:3:pcs,garlic:2:pcs,olive oil:2:tbsp",
            "Scrambled Eggs on Toast#Toast the bread.|Whisk the eggs with salt.|Scramble in melted butter and pile onto the toast.#egg:3:pcs,bread:2:pcs,butter:10:g,salt:1:tsp",
            "Cheese Omelette#Whisk the eggs with salt.|Pour into a buttered pan.|Add grated cheese, fold and serve.#egg:3:pcs,cheese:50:g,butter:10:g,salt:1:tsp",
            "Pancakes#Whisk flour, milk, egg and sugar into a batter.|Pour small rounds into a hot pan.|Flip when bubbles form and cook until golden.#flour:150:g,milk:250:ml,egg:1:pcs,sugar:2:tbsp",
            "Egg Fried Rice#Chop the onion and fry in oil.|Add cooked rice and soy sauce.|Push aside, scramble the eggs, then mix together.#rice:200:g,egg:2:pcs,onion:1:pcs,soy sauce:2:tbsp,vegetable oil:1:tbsp",
            "Garlic Bread#Mash the butter with chopped garlic.|Spread over the bread.|Bake at 200C for 8 minutes.#bread:4:pcs,butter:40:g,garlic:2:pcs",
            "Potato Soup#Peel and cube the potatoes, chop the onion.|Simmer in water until soft.|Blend with milk and butter, season with salt.#potato:4:pcs,onion:1:pcs,milk:200:ml,butter:20:g,salt:1:tsp",
            "Tomato Soup#Fry the onion and garlic.|Add chopped tomatoes and stock.|Simmer for 20 minutes then blend.#tomato:6:pcs,onion:1:pcs,garlic:2:pcs,vegetable stock:500:ml",
            "Chicken Rice Bowl#Cook the rice.|Slice the chicken and fry with garlic.|Add soy sauce and serve over the rice.#chicken breast:300:g,rice:200:g,soy sauce:2:tbsp,garlic:2:pcs",
            "Cheese Toastie#Butter the outside of the bread.|Put the cheese between the slices.|Fry on both sides until golden.#bread:2:pcs,cheese:60:g,butter:10:g",
            "Banana Smoothie#Peel the bananas.|Blend with milk and honey until smooth.#banana:2:pcs,milk:250:ml,honey:1:tbsp",
            "Banana Oatmeal#Heat the oats and milk, stirring, for 5 minutes.|Top with sliced banana and honey.#oats:80:g,milk:250:ml,banana:1:pcs,honey:1:tbsp",
            "Mashed Potatoes#Peel and boil the potatoes until soft.|Drain and mash with butter, milk and salt.#potato:5:pcs,butter:30:g,milk:100:ml,salt:1:tsp",
            "Vegetable Stir-Fry#Slice all the vegetables.|Stir-fry in hot oil for 5 minutes.|Add soy sauce and serve.#carrot:2:pcs,onion:1:pcs,bell pepper:1:pcs,soy sauce:2:tbsp,vegetable oil:1:tbsp",
            "Spaghetti Aglio e Olio#Boil the spaghetti.|Gently fry sliced garlic and chilli flakes in olive oil.|Toss the spaghetti through the oil.#spaghetti:200:g,garlic:4:pcs,olive oil:4:tbsp,chilli flakes:1:tsp",
            "Tuna Pasta#Boil the pasta.|Fry the onion in olive oil.|Add the drained tuna, then toss with the pasta.#pasta:200:g,tuna:1:pcs,onion:1:pcs,olive oil:1:tbsp",
            "French Toast#Whisk the eggs, milk and sugar.|Soak the bread slices.|Fry in butter until golden on both sides.#bread:4:pcs,egg:2:pcs,milk:100:ml,butter:10:g,sugar:1:tbsp",
            "Lentil Curry#Fry the onion and garlic.|Add curry powder, tomatoes and lentils.|Add water and simmer for 25 minutes.#lentil:200:g,onion:1:pcs,tomato:2:pcs,garlic:2:pcs,curry powder:2:tsp"
    };

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL)");
        seedRecipes(db); // runs once, when the database file is first created
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        for (String line : SEED_RECIPES) {
            String[] parts = line.split("#");
            ContentValues rv = new ContentValues();
            rv.put("name", parts[0]);
            rv.put("steps", parts[1].replace("|", "\n"));
            long recipeId = db.insert("recipes", null, rv);

            for (String ing : parts[2].split(",")) {
                String[] f = ing.split(":");
                ContentValues iv = new ContentValues();
                iv.put("recipe_id", recipeId);
                iv.put("name", f[0]);
                iv.put("quantity", Double.parseDouble(f[1]));
                iv.put("unit", f[2]);
                db.insert("recipe_ingredients", null, iv);
            }
        }
    }

    // ---------- Pantry CRUD ----------

    private ContentValues toValues(PantryItem item) {
        ContentValues cv = new ContentValues();
        cv.put("name", item.getName());
        cv.put("quantity", item.getQuantity());
        cv.put("unit", item.getUnit());
        cv.put("expiry", item.getExpiry());
        return cv;
    }

    /** Create */
    public long addPantryItem(PantryItem item) {
        return getWritableDatabase().insert("pantry", null, toValues(item));
    }

    /** Read (all) */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE")) {
            while (c.moveToNext()) list.add(readPantryItem(c));
        }
        return list;
    }

    /** Read (one) */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query("pantry", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readPantryItem(c) : null;
        }
    }

    /** Update */
    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update("pantry", toValues(item), "id=?",
                new String[]{String.valueOf(item.getId())});
    }

    /** Delete */
    public void deletePantryItem(long id) {
        getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    private PantryItem readPantryItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    // ---------- Recipes (read only) ----------

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("recipes", null, null, null, null, null, "name")) {
            while (c.moveToNext()) list.add(readRecipe(c));
        }
        return list;
    }

    public Recipe getRecipe(long id) {
        try (Cursor c = getReadableDatabase().query("recipes", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readRecipe(c) : null;
        }
    }

    private Recipe readRecipe(Cursor c) {
        Recipe r = new Recipe(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("steps")));
        r.setIngredients(getIngredients(r.getId()));
        return r;
    }

    private List<RecipeIngredient> getIngredients(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("recipe_ingredients", null, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, "id")) {
            while (c.moveToNext()) {
                list.add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getDouble(c.getColumnIndexOrThrow("quantity")),
                        c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        }
        return list;
    }
}
