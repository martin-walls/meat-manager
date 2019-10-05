package com.martinwalls.nea;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    //todo make all other activities inherit from this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasyPreferences preferences = EasyPreferences.createForDefaultPreferences(this);
        boolean darkTheme = preferences.getBoolean(R.string.pref_dark_theme, false);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setDarkThemeEnabled(boolean isEnabled) {
        if (isEnabled) {
            //todo set dark theme
        }
    }
}
