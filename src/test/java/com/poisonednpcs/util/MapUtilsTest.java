package com.poisonednpcs.util;

import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;

import java.util.Map;

/** Tests for {@link MapUtils}. */
public class MapUtilsTest extends TestCase {

    public void testTransformValues() {
        Map<Integer, Integer> original = ImmutableMap.of(
                0, 0,
                1, 1,
                2, 2);

        Map<Integer, String> transformed = MapUtils.transformValues(original, (k, i) -> i + "");
        transformed.forEach((key, value) -> assertEquals(key + "", value));
    }

}