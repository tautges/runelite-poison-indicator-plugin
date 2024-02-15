package com.poisonednpcs.health;

import java.util.HashMap;
import java.util.Map;

public enum HitsplatType {
    OPPONENT_SPLASHED_BY_ME(12),
    OPPONENT_SPLASHED_BY_OTHER(13), // TODO: are by-NPC and by-other-player the same?
    OPPONENT_DAMAGED_BY_ME(16),
    OPPONENT_DAMAGED_BY_OTHER(17), // TODO: are by-NPC and by-other-player the same?
    OPPONENT_DAMAGED_BY_POISON(65), // TODO: distinction between poison from a certain source?
    ;

    private final int _enum;

    HitsplatType(int _enum) {
        this._enum = _enum;
    }

    public static HitsplatType of(int _enum) {
        return VALUES.get(_enum);
    }

    private static Map<Integer, HitsplatType> assembleValues() {
        Map<Integer, HitsplatType> values = new HashMap<>();
        for (HitsplatType value : HitsplatType.values()) {
            values.put(value._enum, value);
        }
        return values;
    }

    public static final Map<Integer, HitsplatType> VALUES = assembleValues();
}
