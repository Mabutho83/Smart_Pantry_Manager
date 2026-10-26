package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import model.*;

/** Suggested Recipes screen: lists only recipes that pass the strict-matching rule. */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Suggested Recipes");

        DatabaseHelper db = new DatabaseHelper(this);
        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> allRecipes = db.getAllRecipes();

        // The strict-matching rule is applied here.
        List<Recipe> matches = RecipeMatcher.findStrictMatches(pantry, allRecipes);

        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new RecipeAdapter(matches, this));

        boolean empty = matches.isEmpty();
        findViewById(R.id.tvNoRecipes).setVisibility(empty ? View.VISIBLE : View.GONE);

        TextView tvCount = findViewById(R.id.tvRecipeCount);
        tvCount.setVisibility(empty ? View.GONE : View.VISIBLE);
        int n = matches.size();
        tvCount.setText(n + (n == 1 ? " recipe you can cook right now" : " recipes you can cook right now"));

        BottomNavHelper.setup(this, R.id.nav_recipes);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
