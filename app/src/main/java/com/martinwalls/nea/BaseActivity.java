package com.martinwalls.nea;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EasyPreferences preferences = EasyPreferences.createForDefaultPreferences(this);
//        boolean isDarkThemeEnabled = preferences.getBoolean(R.string.pref_dark_theme, false);
//        setDarkThemeEnabled(isDarkThemeEnabled);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        boolean darkTheme = preferences.getBoolean(R.string.pref_dark_theme, false);
//        if (isDarkThemeEnabled != darkTheme) {
//            recreate();
//        }
//    }

    protected void setDarkThemeEnabled(boolean isEnabled) {
//        if (isEnabled) {
////            setTheme(R.style.AppTheme_Dark);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
////            setTheme(R.style.AppTheme_Light);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
    }
}
