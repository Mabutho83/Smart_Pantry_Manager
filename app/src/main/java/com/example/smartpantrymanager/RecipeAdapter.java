package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import model.*;

/** Binds the list of suggested recipes to item_recipe.xml rows. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final Listener listener;

    public RecipeAdapter(List<Recipe> recipes, Listener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvName.setText(recipe.getName());

        int count = recipe.getIngredients().size();
        holder.tvMeta.setText(count + (count == 1 ? " ingredient" : " ingredients"));

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ing.getName());
        }
        holder.tvIngredients.setText(sb.toString());

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeta, tvIngredients;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvRecipeName);
            tvMeta = v.findViewById(R.id.tvRecipeMeta);
            tvIngredients = v.findViewById(R.id.tvRecipeIngredients);
        }
    }
}
