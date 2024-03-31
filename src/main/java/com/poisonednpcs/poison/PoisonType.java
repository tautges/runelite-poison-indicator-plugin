package com.poisonednpcs.poison;

import com.google.common.collect.Iterables;
import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;

import static com.poisonednpcs.poison.Poison.*;

public enum PoisonType {
    // TODO: avoid double-declaring the regular expressions

    RANGED(false, RANGED_POISON_PROGRESSION, Pattern.compile("\\(p\\)$")),
    RANGED_PLUS(false, RANGED_POISON_PLUS_PROGRESSION, Pattern.compile("\\(p\\+\\)$")),
    RANGED_PLUS_PLUS(false, RANGED_POISON_PLUS_PLUS_PROGRESSION, Pattern.compile("\\(p\\+\\+\\)$")),
    MELEE(true, MELEE_POISON_PROGRESSION, Pattern.compile("\\(p\\)$")),
    MELEE_PLUS(true, MELEE_POISON_PLUS_PROGRESSION, Pattern.compile("\\(p\\+\\)$")),
    MELEE_PLUS_PLUS(true, MELEE_POISON_PLUS_PLUS_PROGRESSION, Pattern.compile("\\(p\\+\\+\\)$")),
    KARAMBWAN(true, MELEE_POISON_PLUS_PLUS_PROGRESSION, Pattern.compile("\\(kp\\)$")),
    // Despite being ranged, enchanted emerald bolts use a progression equivalent to melee poison+ w/an initial 5
    EMERALD_BOLTS_E(false, MELEE_POISON_PLUS_PROGRESSION, Pattern.compile("Emerald bolts \\(e\\)"))
    ;

    private final boolean isMelee;
    @Getter
    private final int[] progression;
    @Getter
    private final Pattern weaponRegex;

    PoisonType(boolean isMelee, int[] progression, Pattern weaponRegex) {
        this.isMelee = isMelee;
        this.progression = progression;
        this.weaponRegex = weaponRegex;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public int maxHit() {
        return Arrays.stream(progression).max().orElse(0);
    }

    // This could be done statically.
    public static Iterable<PoisonType> onlyMelee() {
        return Iterables.filter(Arrays.asList(PoisonType.values()), PoisonType::isMelee);
    }
}
