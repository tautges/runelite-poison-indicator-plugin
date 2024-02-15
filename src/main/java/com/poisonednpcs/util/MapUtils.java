package com.poisonednpcs.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class MapUtils {

    public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> original, BiFunction<K, V1, V2> transformer) {
        Map<K, V2> transformed = new HashMap<>();
        original.forEach((k, v1) -> transformed.put(k, transformer.apply(k, v1)));
        return transformed;
    }
}
