package com.martinwalls.nea.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.annotation.StringRes;
import androidx.preference.PreferenceManager;

/**
 * Helper class for accessing SharedPreferences more easily. Forces the use
 * of String resources as keys rather than plain String values to make
 * sure the same keys are used throughout the app, and to avoid errors from
 * mistyping a key.
 */
public final class EasyPreferences {

    private final SharedPreferences prefs;
    private final Resources res;

    /**
     * Creates an {@link EasyPreferences} object. This is used instead of
     * the constructor to create an instance.
     */
    public static EasyPreferences createForDefaultPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();
        return new EasyPreferences(preferences, resources);
    }

    /**
     * Private constructor as this should only be created via the factory
     * method and not instantiated.
     */
    private EasyPreferences(SharedPreferences prefs, Resources res) {
        this.prefs = prefs;
        this.res = res;
    }

    /**
     * Gets the string value of the key at the specified resource value.
     *
     * @param key String resource id of the key
     */
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

    /**
     * Gets an int stored as a String value, for example stored from
     * a Preference that stores strings.
     *
     * @throws NumberFormatException if the string value cannot be converted to an int
     */
    public int getIntFromString(@StringRes int keyId, int defaultValue)
            throws NumberFormatException {
        return Integer.parseInt(prefs.getString(key(keyId), String.valueOf(defaultValue)));
    }

    /**
     * Stores an int value as a String.
     */
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
}
