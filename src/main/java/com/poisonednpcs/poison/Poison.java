package com.poisonednpcs.poison;

import java.time.Duration;

import static com.poisonednpcs.util.ArrayUtils.arrayOf;
import static com.poisonednpcs.util.ArrayUtils.concat;

public class Poison {

    // TODO: there's probably a library somewhere that has this value
    /** Length of time for one game tick, equal to 600ms. */
    public static final Duration GAME_TICK = Duration.ofMillis(600);
    /** The number of ticks it takes for poison to first splat, assuming it was applied. */
    private static final int POISON_DELAY_IN_TICKS = 30;

    public static final Duration POISON_DELAY = GAME_TICK.multipliedBy(POISON_DELAY_IN_TICKS); // 18 seconds

    static final int[] MELEE_POISON_PROGRESSION =
            arrayOf(4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1);
    static final int[] MELEE_POISON_PLUS_PROGRESSION =
            concat(arrayOf(5, 5, 5, 5, 5), MELEE_POISON_PROGRESSION);
    static final int[] MELEE_POISON_PLUS_PLUS_PROGRESSION =
            concat(arrayOf(6, 6, 6, 6, 6), MELEE_POISON_PLUS_PROGRESSION);

    static final int[] RANGED_POISON_PROGRESSION =
            arrayOf(2, 1, 1, 1, 1, 1);
    static final int[] RANGED_POISON_PLUS_PROGRESSION =
            concat(arrayOf(3, 2, 2, 2, 2), RANGED_POISON_PROGRESSION);
    static final int[] RANGED_POISON_PLUS_PLUS_PROGRESSION =
            concat(arrayOf(4, 3, 3, 3, 3), RANGED_POISON_PLUS_PROGRESSION);
}
