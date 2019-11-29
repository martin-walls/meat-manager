package com.martinwalls.meatmanager.util;

import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;

public final class AppTheme {
    public static final int MODE_AUTO = 0;
    public static final int MODE_LIGHT = 1;
    public static final int MODE_DARK = 2;

    private AppTheme() {}

    /**
     * Sets the app's theme to dark, light or auto depending on {@code mode}.
     */
    public static void setDarkTheme(int mode) {
        switch (mode) {
            case MODE_AUTO:
                // "follow system" only available in Android 9 or later
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
