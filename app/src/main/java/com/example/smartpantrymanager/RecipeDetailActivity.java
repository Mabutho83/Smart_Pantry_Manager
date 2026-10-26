package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import model.*;
/** Recipe Detail screen: full ingredient list and method for the recipe passed in via Intent. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(id);
        if (recipe == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvDetailTitle)).setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            sb.append("\u2022 ")
                    .append(Units.format(ing.getQuantity())).append(" ")
                    .append(ing.getUnit()).append(" ")
                    .append(ing.getName()).append("\n");
        }
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(sb.toString().trim());

        // Number each step of the method
        String[] steps = recipe.getSteps().split("\n");
        StringBuilder method = new StringBuilder();
        for (int i = 0; i < steps.length; i++) {
            method.append(i + 1).append(". ").append(steps[i]).append("\n\n");
        }
        ((TextView) findViewById(R.id.tvDetailSteps)).setText(method.toString().trim());

        ((TextView) findViewById(R.id.tvDetailMeta)).setText(
                recipe.getIngredients().size() + " ingredients \u00B7 " + steps.length + " steps");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
