package com.martinwalls.nea.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.annotation.StringRes;
import androidx.preference.PreferenceManager;

public class EasyPreferences {

    private final SharedPreferences prefs;
    private final Resources res;

    public static EasyPreferences createForDefaultPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();
        return new EasyPreferences(preferences, resources);
    }

    private EasyPreferences(SharedPreferences prefs, Resources res) {
        this.prefs = prefs;
        this.res = res;
    }

    private String key(int key) {
        return res.getString(key);
    }

    public boolean getBoolean(@StringRes int keyId, boolean defaultValue) {
        return prefs.getBoolean(key(keyId), defaultValue);
    }

    public void setBoolean(@StringRes int keyId, boolean value) {
        prefs.edit().putBoolean(key(keyId), value).apply();
    }

    public String getString(@StringRes int keyId, String defaultValue) {
        return prefs.getString(key(keyId), defaultValue);
    }

    public void setString(@StringRes int keyId, String value) {
        prefs.edit().putString(key(keyId), value).apply();
    }

    public int getInt(@StringRes int keyId, int defaultValue) {
        return prefs.getInt(key(keyId), defaultValue);
    }

    public void setInt(@StringRes int keyId, int value) {
        prefs.edit().putInt(key(keyId), value).apply();
    }

    public int getIntFromString(@StringRes int keyId, int defaultValue) {
        return Integer.parseInt(prefs.getString(key(keyId), String.valueOf(defaultValue)));
    }

    public void setIntToString(@StringRes int keyId, int value) {
        prefs.edit().putString(key(keyId), String.valueOf(value)).apply();
    }

    public long getLong(@StringRes int keyId, long defaultValue) {
        return prefs.getLong(key(keyId), defaultValue);
    }

    public void setLong(@StringRes int keyId, long value) {
        prefs.edit().putLong(key(keyId), value).apply();
    }

    public float getFloat(@StringRes int keyId, float defaultValue) {
        return prefs.getFloat(key(keyId), defaultValue);
    }

    public void setFloat(@StringRes int keyId, float value) {
        prefs.edit().putFloat(key(keyId), value).apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public void addOnPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeOnPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
