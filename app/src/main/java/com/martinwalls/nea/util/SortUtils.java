package com.martinwalls.nea.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortUtils {
    // constants to store sort modes throughout app
    /**
     * Sort by name alphabetically (A -> Z)
     */
    public static final int SORT_NAME = 0;
    /**
     * Sort by meat type alphabetically (A -> Z)
     */
    public static final int SORT_MEAT_TYPE = 1;
    /**
     * Sort by amount (low -> high)
     */
    public static final int SORT_AMOUNT_ASC = 2;
    /**
     * Sort by amount (high -> low)
     */
    public static final int SORT_AMOUNT_DESC = 3;
    /**
     * Sort by location alphabetically (A -> Z)
     */
    public static final int SORT_LOCATION = 4;

    private SortUtils() {}

    /**
     * Merge sort algorithm (recursive). Takes a list of objects
     * that implement {@link Comparable}.
     *
     * @param list A list of objects that must implement {@link Comparable}
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
}
