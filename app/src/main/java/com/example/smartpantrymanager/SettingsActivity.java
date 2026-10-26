package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/** Settings screen: toggle for highlighting items that are expiring soon on the pantry list. */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "smart_pantry_prefs";
    public static final String KEY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SwitchCompat switchAlerts = findViewById(R.id.switchAlerts);
        switchAlerts.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        switchAlerts.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean(KEY_ALERTS, checked).apply());
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
