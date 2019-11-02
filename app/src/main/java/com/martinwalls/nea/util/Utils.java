package com.martinwalls.nea.util;

import android.content.Context;
import android.util.TypedValue;
import androidx.annotation.AttrRes;

import java.text.DecimalFormat;

public final class Utils {
    // 1 kg = 2.20462 lbs
    private static final double kgsToLbs = 2.20462262185;
    // 1 lb = 0.453592 kgs
    private static final double lbsToKgs = 0.45359237;

    // Utils shouldn't be instantiated
    private Utils() {}

    public static double convertToLbs(double kgs) {
        return kgs / lbsToKgs;
    }

    public static double convertToKgs(double lbs) {
        return lbs * lbsToKgs;
    }

    /**
     * Get the current setting for kg/lbs and convert the kg value to lbs if appropriate.
     */
    public static double convertToCurrentMassUnit(Context context, double kgs) {
        if (MassUnit.getMassUnit(context) == MassUnit.LBS) {
            return convertToLbs(kgs);
        } else {
            return kgs;
        }
    }

    /**
     * Get the kg value of a mass that is stored in the current mass unit.
     */
    public static double getKgsFromCurrentMassUnit(Context context, double mass) {
        if (MassUnit.getMassUnit(context) == MassUnit.LBS) {
            return convertToKgs(mass);
        } else {
            return mass;
        }
    }

    public static String getMassDisplayValue(Context context, double mass, int dp) {
        return roundToDp(convertToCurrentMassUnit(context, mass), dp);
    }

    public static String roundToDp(double value, int dp) {
        if (dp == 0) {
            return new DecimalFormat("#").format(value);
        }
        StringBuilder pattern = new StringBuilder("#.0");
        for (int i = 0; i < dp - 1; i++) {
            pattern.append("#");
        }
        return new DecimalFormat(pattern.toString()).format(value);
    }

    /**
     * Converts a dp value to pixels, depending on the display density.
     * @param dpValue A dp value (display independent pixels)
     * @param context Context needed to get display density
     */
    public static int convertDpToPixelSize(float dpValue, Context context) {
//        final float density = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * density + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }

    public static int convertSpToPixelSize(float spValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

    public static int getColorFromTheme(Context context, @AttrRes int resId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, outValue, true);
        return outValue.data;
    }
}
