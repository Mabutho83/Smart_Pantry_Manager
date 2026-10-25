package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import model.PantryItem;


/** Pantry List screen: shows every ingredient and lets the user add, edit or delete them. */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My Pantry");

        db = new DatabaseHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditItemActivity.class)));
    }

    // Reload whenever we come back from the add/edit or settings screens.
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.getAllPantryItems();
        boolean alerts = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE)
                .getBoolean(SettingsActivity.KEY_ALERTS, true);
        adapter.setData(items, alerts);
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Delete " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_suggested) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
