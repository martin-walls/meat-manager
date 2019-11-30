package com.martinwalls.meatmanager.util;

import android.content.Context;
import android.content.res.Configuration;
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
    public static void setAppTheme(int mode) {
        switch (mode) {
            case MODE_AUTO:
                // "follow system" only available in Android 10 or later
                // (technically can be enabled in Android 9 in dev options)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
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

    /**
     * Gets the current night mode of the system, that the user has chosen in
     * their settings. Returns light mode if system theme not defined.
     */
    public static int getCurrentSystemTheme(Context context) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                return MODE_DARK;
            default:
            case Configuration.UI_MODE_NIGHT_NO:
                return MODE_LIGHT;
        }
    }
}
