package com.poisonednpcs.util;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

/** Tests for {@link ArrayUtils}. */
public class ArrayUtilsTest extends TestCase {

    public void testConcat() {
        int[] one = new int[] {1, 2, 3};
        int[] two = new int[] {4, 5, 6};

        int[] concatenated = ArrayUtils.concat(one, two);
        for (int i = 0; i < concatenated.length; i++) {
            assertEquals(i + 1, concatenated[i]);
        }
    }

    public void testFilter() {
        String[] original = new String[] { "", "_", "__", "___", "____"};
        Iterable<String> filtered = ArrayUtils.filter(original, s -> (s.length() % 2) == 1);

        Set<String> setified = new HashSet<>();
        filtered.forEach(setified::add);
        assertEquals(2, setified.size());
        assertEquals(true, setified.contains("_"));
        assertEquals(true, setified.contains("___"));
    }

}