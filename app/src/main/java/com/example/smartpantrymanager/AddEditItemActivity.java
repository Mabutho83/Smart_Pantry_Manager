package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import model.*;

/** Add/Edit Ingredient screen. Receives an item id via Intent when editing, none when adding. */
public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private DatabaseHelper db;
    private EditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private long itemId = -1; // -1 means "adding a new item"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnClearDate = findViewById(R.id.btnClearDate);

        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Units.ALL));

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> saveItem());
        btnDelete.setOnClickListener(v -> confirmDelete());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            setTitle("Edit Ingredient");
            btnDelete.setVisibility(View.VISIBLE);
            PantryItem item = db.getPantryItem(itemId);
            if (item != null) {
                etName.setText(item.getName());
                etQuantity.setText(Units.format(item.getQuantity()));
                spUnit.setSelection(Arrays.asList(Units.ALL).indexOf(item.getUnit()));
                etExpiry.setText(item.getExpiry());
            }
        } else {
            setTitle("Add Ingredient");
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) ->
                        etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();

        // Input validation
        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            return;
        }
        double qty;
        try {
            qty = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            return;
        }
        if (qty <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            return;
        }

        String unit = spUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();
        if (expiry.isEmpty()) expiry = null; // expiry is optional

        if (itemId == -1) {
            db.addPantryItem(new PantryItem(0, name, qty, unit, expiry));
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(new PantryItem(itemId, name, qty, unit, expiry));
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Delete this ingredient from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deletePantryItem(itemId);
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
