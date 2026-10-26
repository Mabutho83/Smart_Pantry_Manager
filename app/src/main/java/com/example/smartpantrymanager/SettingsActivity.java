package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

/** Settings screen: toggle for highlighting items that are expiring soon on the pantry list. */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "smart_pantry_prefs";
    public static final String KEY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        MaterialSwitch switchAlerts = findViewById(R.id.switchAlerts);
        switchAlerts.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        switchAlerts.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean(KEY_ALERTS, checked).apply());

        BottomNavHelper.setup(this, R.id.nav_settings);
    }
}
