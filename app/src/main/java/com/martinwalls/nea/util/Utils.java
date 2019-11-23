package com.martinwalls.nea.util;

import android.content.Context;
import android.util.TypedValue;
import androidx.annotation.AttrRes;

import java.text.DecimalFormat;

public final class Utils {
    // 1 lb ~= 0.45359237 kgs
    private static final double lbsToKgs = 0.45359237;

    // Utils shouldn't be instantiated
    private Utils() {}

    /**
     * Converts a value from kgs to lbs.
     */
    private static double convertToLbs(double kgs) {
        return kgs / lbsToKgs;
    }

    /**
     * Converts a value from lbs to kgs.
     */
    private static double convertToKgs(double lbs) {
        return lbs * lbsToKgs;
    }

    /**
     * Gets the current setting for kg/lbs and converts the kg value to lbs if
     * appropriate.
     */
    public static double convertToCurrentMassUnit(Context context, double kgs) {
        if (MassUnit.getMassUnit(context) == MassUnit.LBS) {
            return convertToLbs(kgs);
        } else {
            return kgs;
        }
    }

    /**
     * Gets the kg value of a mass that is stored in the current mass unit.
     */
    public static double getKgsFromCurrentMassUnit(Context context, double mass) {
        if (MassUnit.getMassUnit(context) == MassUnit.LBS) {
            return convertToKgs(mass);
        } else {
            return mass;
        }
    }

    /**
     * Converts the {@code mass} value to the mass unit currently in use
     * and rounds it to {@code dp} decimal places, to use when displaying
     * mass values on screen.
     */
    public static String getMassDisplayValue(Context context, double mass, int dp) {
        return roundToDp(convertToCurrentMassUnit(context, mass), dp);
    }

    /**
     * Rounds {@code value} to {@code dp} decimal places.
     */
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
     * Converts a dp value to pixels.
     */
    public static int convertDpToPixelSize(float dpValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * Converts an sp value (for text sizes) to pixels.
     */
    public static int convertSpToPixelSize(float spValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * Gets a colour value from a theme attribute.
     */
    public static int getColorFromTheme(Context context, @AttrRes int resId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, outValue, true);
        return outValue.data;
    }
}
