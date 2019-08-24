package com.martinwalls.nea;

import java.util.ArrayList;
import java.util.List;

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

    public static <T extends Comparable<T>> List<T> mergeSort(List<T> list) {
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
}
