package com.martinwalls.nea;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // load theme on startup
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(this);
        DarkTheme.setDarkTheme(prefs.getIntFromString(R.string.pref_dark_theme, DarkTheme.MODE_NIGHT_AUTO));

        // start on default page so clear saved position
        prefs.setString(R.string.pref_last_opened_page, null);
    }
}
