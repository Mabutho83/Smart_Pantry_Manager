package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import model.PantryItem;

/** Pantry List screen: shows every ingredient and lets the user add, edit or delete them. */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private View emptyState, summaryCard;
    private TextView tvSummaryTitle, tvSummarySub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Pantry");

        db = new DatabaseHelper(this);
        emptyState = findViewById(R.id.tvEmpty);
        summaryCard = findViewById(R.id.summaryCard);
        tvSummaryTitle = findViewById(R.id.tvSummaryTitle);
        tvSummarySub = findViewById(R.id.tvSummarySub);

        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recycler.setAdapter(adapter);

        View fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditItemActivity.class)));

        BottomNavHelper.setup(this, R.id.nav_pantry);
    }

    // Reload whenever we come back from the add/edit or other screens.
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

        boolean empty = items.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        summaryCard.setVisibility(empty ? View.GONE : View.VISIBLE);

        if (!empty) {
            int ready = RecipeMatcher.findStrictMatches(items, db.getAllRecipes()).size();
            int n = items.size();
            tvSummaryTitle.setText(n + (n == 1 ? " ingredient" : " ingredients") + " in your pantry");
            tvSummarySub.setText(ready == 0
                    ? "No recipes ready yet"
                    : ready + (ready == 1 ? " recipe" : " recipes") + " ready to cook");
        }
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete ingredient")
                .setMessage("Delete " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
