package com.martinwalls.nea.util;

import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;

public final class DarkTheme {
    public static final int MODE_NIGHT_AUTO = 0;
    public static final int MODE_NIGHT_NO = 1;
    public static final int MODE_NIGHT_YES = 2;

    /**
     * Sets the app's theme to dark, light or auto depending on {@code mode}.
     */
    public static void setDarkTheme(int mode) {
        switch (mode) {
            case MODE_NIGHT_AUTO:
                // "follow system" only available in Android 9 or later
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
            case MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
