package com.martinwalls.nea.util;

import android.content.Context;
import android.util.TypedValue;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Utils {
    // 1 kg = 2.20462 lbs
    private static final double kgsToLbs = 2.20462;
    // 1 lb = 0.453592 kgs
    private static final double lbsToKgs = 0.453592;

    // Utils shouldn't be instantiated
    private Utils() {}

    public static double convertToLbs(double kgs) {
        return kgs * kgsToLbs;
    }

    public static double convertToKgs(double lbs) {
        return lbs * lbsToKgs;
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

    /**
     * Merge sort algorithm (recursive). Takes a list of objects
     * that implement {@link Comparable}.
     */
    public static <T extends Comparable<T>> List<T> mergeSort(@NonNull List<T> list) {
        return mergeSort(list, (o1, o2) -> o1.compareTo(o2));
    }

    /**
     * Merge sort algorithm (recursive). Takes a list of objects and
     * a {@link Comparator} describing how to compare objects.
     */
    public static <T> List<T> mergeSort(@NonNull List<T> list, Comparator<T> comparator) {
        if (list.size() <= 1) {
            return list;
        }

        int midpoint = list.size() / 2;
        List<T> firstHalf = list.subList(0, midpoint);
        List<T> lastHalf = list.subList(midpoint, list.size());

        firstHalf = mergeSort(firstHalf, comparator);
        lastHalf = mergeSort(lastHalf, comparator);

        int i = 0, j = 0;
        List<T> merged = new ArrayList<>();

        while (i < firstHalf.size() && j < lastHalf.size()) {
            if (comparator.compare(firstHalf.get(i), lastHalf.get(j)) < 0) {
                merged.add(firstHalf.get(i));
                i++;
            } else {
                merged.add(lastHalf.get(j));
                j++;
            }
        }

        while (i < firstHalf.size()) {
            merged.add(firstHalf.get(i));
            i++;
        }

        while (j < lastHalf.size()) {
            merged.add(lastHalf.get(j));
            j++;
        }

        return merged;
    }

    public static int getColorFromTheme(Context context, @AttrRes int resId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, outValue, true);
        return outValue.data;
    }
}
