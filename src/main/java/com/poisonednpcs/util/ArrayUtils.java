package com.poisonednpcs.util;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.Predicate;

/**
 * Utilities to help with operations affected on arrays. There are libraries which do similar things, but these are
 * defined simplistically to complete necessary operations without concerning ourselves with operational overhead.
 */
public class ArrayUtils {

    public static int[] arrayOf(int... nums) {
        return nums;
    }

    /**
     * Concatenates an arbitrary number of integer arrays, in the order in which they are specified.
     */
    public static int[] concat(int[]... arrs) {
        int sum = 0;
        for (int[] arr : arrs) {
            sum += arr.length;
        }
        int[] result = new int[sum];
        Iterator<int[]> arr = Arrays.stream(arrs).iterator();
        int i = 0;
        while (arr.hasNext()) {
            int[] next = arr.next();
            for (int num : next) {
                result[i++] = num;
            }
        }
        return result;
    }

    /**
     * Filters values from an array based on the specified predicate. Values passing true on the predicate are retained.
     */
    public static <K> Iterable<K> filter(K[] values, Predicate<K> filter) {
        ImmutableList.Builder<K> result = ImmutableList.builder();
        for (K k : values) {
            if (filter.test(k)) {
                result.add(k);
            }
        }
        return result.build();
    }
}
