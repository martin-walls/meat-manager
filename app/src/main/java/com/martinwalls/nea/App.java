package com.martinwalls.nea;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // load theme on startup
        EasyPreferences preferences = EasyPreferences.createForDefaultPreferences(this);
        if (preferences.getBoolean(R.string.pref_dark_theme, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
