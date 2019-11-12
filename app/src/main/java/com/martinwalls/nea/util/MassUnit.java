package com.martinwalls.nea.util;

import android.content.Context;
import com.martinwalls.nea.R;

public enum MassUnit {
    KG,
    LBS;

    /**
     * Gets the current setting for mass unit as chosen by the user in settings.
     * Returns a {@link MassUnit} object corresponding to the current setting.
     */
    public static MassUnit getMassUnit(Context context) {
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(context);
        if (prefs.getString(R.string.pref_mass_unit, "kg").equals("kg")) {
            return KG;
        } else {
            return LBS;
        }
    }
}
