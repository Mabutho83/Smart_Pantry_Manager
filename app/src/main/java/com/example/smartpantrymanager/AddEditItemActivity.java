package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;
import model.*;

/** Add/Edit Ingredient screen. Receives an item id via Intent when editing, none when adding. */
public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private DatabaseHelper db;
    private TextInputLayout tilName, tilQuantity;
    private EditText etName, etQuantity, etExpiry;
    private MaterialAutoCompleteTextView acUnit;
    private long itemId = -1; // -1 means "adding a new item"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        acUnit = findViewById(R.id.acUnit);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);
        MaterialButton btnClearDate = findViewById(R.id.btnClearDate);

        // Unit dropdown (defaults to the first unit)
        acUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Units.ALL));
        acUnit.setText(Units.ALL[0], false);

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> saveItem());
        btnDelete.setOnClickListener(v -> confirmDelete());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            getSupportActionBar().setTitle("Edit Ingredient");
            btnDelete.setVisibility(View.VISIBLE);
            PantryItem item = db.getPantryItem(itemId);
            if (item != null) {
                etName.setText(item.getName());
                etQuantity.setText(Units.format(item.getQuantity()));
                acUnit.setText(item.getUnit(), false);
                etExpiry.setText(item.getExpiry());
            }
        } else {
            getSupportActionBar().setTitle("Add Ingredient");
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
        tilName.setError(null);
        tilQuantity.setError(null);

        // Input validation
        if (name.isEmpty()) {
            tilName.setError("Ingredient name is required");
            return;
        }
        double qty;
        try {
            qty = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            tilQuantity.setError("Enter a valid number");
            return;
        }
        if (qty <= 0) {
            tilQuantity.setError("Must be greater than 0");
            return;
        }

        String unit = acUnit.getText().toString().trim();
        if (unit.isEmpty()) unit = Units.ALL[0];
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
        new MaterialAlertDialogBuilder(this)
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
