package com.martinwalls.nea;

public class Utils {
    // 1 kg = 2.20462 lbs
    private static final double kgsToLbs = 2.20462;
    // 1 lb = 0.453592 kgs
    private static final double lbsToKgs = 0.453592;

    public static double convertToLbs(double kgs) {
        return kgs * kgsToLbs;
    }

    public static double convertToKgs(double lbs) {
        return lbs * lbsToKgs;
    }
}
