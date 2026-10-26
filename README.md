# Smart Pantry Manager

An Android app written in **Java** that helps reduce food waste. The user records the ingredients they
actually have at home, and the app suggests only the recipes they can cook **right now**, with no shopping
trip required. A recipe is never suggested unless the pantry contains everything it needs.

Built for the *Mobile App Development 700* practical assignment.

- **Author:** Mabutho Mthethwa
- **Language:** Java (no Kotlin)
- **Database:** SQLite (`SQLiteOpenHelper`)
- **Min SDK:** 24 (Android 7.0)

---

## Features

- **Pantry management:** add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- **Pantry list:** a `RecyclerView` with a custom adapter, bound to the database
- **Input validation:** the name is required, and the quantity must be a number greater than 0
- **Recipe collection:** 18 recipes pre-loaded into the database on first run
- **Suggested Recipes:** lists only recipes that pass the strict-matching rule (below)
- **Recipe detail:** full ingredient list and numbered method
- **Settings:** toggle to highlight items that are expiring soon (stored in `SharedPreferences`)
- **Empty states:** friendly messages when the pantry is empty or no recipes match

## The strict-matching rule

A recipe appears under *Suggested Recipes* **only if every ingredient it requires is in the pantry in at
least the required quantity**. If a recipe needs 5 ingredients and the pantry has 4, it is excluded.

The matching is deliberately tolerant of simple real-world messiness:

- **Case and plurals:** `Tomatoes`, `tomato` and `TOMATO` are treated as the same ingredient
- **Unit conversion:** quantities are compared within a unit family
  - mass: `g`, `kg`
  - volume: `ml`, `l`, `tsp`, `tbsp`, `cup`
  - count: `pcs`
- **Duplicate entries:** if the same ingredient is added twice, the quantities are added together
- **Incompatible units:** if the pantry and recipe use units that cannot be compared (for example salt in
  grams versus a recipe in teaspoons), having the ingredient counts as enough

The logic lives in `RecipeMatcher.java`.

## Database choice: SQLite

SQLite was chosen because the app is entirely self-contained and on-device. It needs no account, network
connection or backend server, it is built into Android, and it persists data across app restarts. The data
model is small and relational (one recipe has many ingredients), which suits SQL tables well.

### Data model

| Table | Columns |
|---|---|
| `pantry` | `id` (PK), `name`, `quantity`, `unit`, `expiry` (optional) |
| `recipes` | `id` (PK), `name`, `steps` |
| `recipe_ingredients` | `id` (PK), `recipe_id` (FK to `recipes.id`), `name`, `quantity`, `unit` |

Pantry items have full CRUD. Recipes are seeded once in `DatabaseHelper.onCreate()` and are read-only.

## Screens

| Screen | Class | Purpose |
|---|---|---|
| Pantry List | `MainActivity` | Shows all ingredients; the entry point |
| Add/Edit Ingredient | `AddEditItemActivity` | Create and update items (also delete when editing) |
| Suggested Recipes | `SuggestedRecipesActivity` | Recipes that pass the strict-matching rule |
| Recipe Detail | `RecipeDetailActivity` | Ingredients and method for one recipe |
| Settings | `SettingsActivity` | Expiring-soon highlight toggle |

Navigation uses the toolbar overflow menu on the Pantry List. Data is passed between screens with Intent
extras (an item id or recipe id).

## Project structure

```
app/src/main/java/com/example/smartpantry/
├── MainActivity.java              Pantry list
├── AddEditItemActivity.java       Add / edit / delete an ingredient
├── SuggestedRecipesActivity.java  Runs the matcher and lists results
├── RecipeDetailActivity.java      Recipe ingredients and method
├── SettingsActivity.java          Preferences
├── PantryAdapter.java             RecyclerView adapter for pantry rows
├── RecipeAdapter.java             RecyclerView adapter for recipe rows
├── DatabaseHelper.java            SQLite schema, CRUD and recipe seeding
├── RecipeMatcher.java             Strict-matching logic
├── Units.java                     Unit families and conversion
├── PantryItem.java                Model
├── Recipe.java                    Model
└── RecipeIngredient.java          Model
```

## Setup and run

**Requirements:** Android Studio (Giraffe or newer), JDK 11+, an emulator or device running Android 7.0
(API 24) or higher.

1. Clone the repository:
2. Open the project folder in **Android Studio**.
3. Wait for Gradle to sync.
4. Choose an emulator or a connected device.
5. Press **Run**.

No API keys or extra configuration are needed. The app does not use the network, Google Maps, or location
services.

> **Note:** recipes are seeded only when the database is first created. If you change the schema or seed
> data during development, uninstall the app from the emulator before running again.

## Quick demo of the matching rule

1. Open **Suggested Recipes** with an empty pantry and see the "no recipes match" message.
2. Add `Bread` 2 pcs, `Cheese` 60 g and `Butter` 10 g, and **Cheese Toastie** appears.
3. Delete the butter, and **Cheese Toastie** disappears.

## Known limitations

- Ingredient names must match after case and plural normalisation, so different wording
  (for example "spring onion" versus "onion") is not matched
- The expiring-soon setting highlights items on the list; it does not send notifications
- Expired items still count towards recipe matching
- Recipes cannot be added or edited from within the app
