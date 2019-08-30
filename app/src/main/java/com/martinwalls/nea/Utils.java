package com.martinwalls.nea;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utils {
    // 1 kg = 2.20462 lbs
    private static final double kgsToLbs = 2.20462;
    // 1 lb = 0.453592 kgs
    private static final double lbsToKgs = 0.453592;

    private Utils() { /* private so it can't be instantiated */ }

    public static double convertToLbs(double kgs) {
        return kgs * kgsToLbs;
    }

    public static double convertToKgs(double lbs) {
        return lbs * lbsToKgs;
    }

    public static int convertDpToPixelSize(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Merge sort algorithm (recursive). Takes a list of objects that implement {@link Comparable}.
     */
    public static <T extends Comparable<T>> List<T> mergeSort(@NonNull List<T> list) {
        if (list.size() <= 1) {
            return list;
        }

        int midpoint = list.size() / 2;
        List<T> firstHalf = list.subList(0, midpoint);
        List<T> lastHalf = list.subList(midpoint, list.size());

        firstHalf = mergeSort(firstHalf);
        lastHalf = mergeSort(lastHalf);

        int i = 0, j = 0;
        List<T> merged = new ArrayList<>();

        while (i < firstHalf.size() && j < lastHalf.size()) {
            if (firstHalf.get(i).compareTo(lastHalf.get(j)) < 0) {
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

    /**
     * Merge sort algorithm (recursive). Takes a list of objects and a {@link Comparator} describing how to compare objects.
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
}
