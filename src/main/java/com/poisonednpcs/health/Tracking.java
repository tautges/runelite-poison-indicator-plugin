package com.poisonednpcs.health;

import com.poisonednpcs.combat.Hit;
import com.poisonednpcs.combat.HitTracker;
import com.poisonednpcs.combat.PoisonState;
import com.poisonednpcs.combat.PoisonTracker;
import com.poisonednpcs.poison.Poison;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Utilities regarding tracking delays between hits, poison splats, and expected damage.
 */
public class Tracking {

    /**
     * Based on {@link Hit} information from the {@link HitTracker}, calculates and returns the duration until the
     * next possible time at which poison can occur.
     */
    public static Duration timeUntilPossiblePoison(HitTracker tracker) {
        Optional<Hit> oldestHit = tracker.getOldestHitAfter(Instant.now().minus(Poison.POISON_DELAY));
        if (oldestHit.isEmpty()) {
            return Duration.ZERO;
        }
        return Duration.between(Instant.now(), oldestHit.get().getOccurredAt()).plus(Poison.POISON_DELAY);
    }

    /**
     * Based on the {@link PoisonState}, determines when the next poison splat will occur.
     */
    public static Duration timeUntilPoisonSplat(PoisonState poisonState) {
        return Duration.between(Instant.now(), PoisonTracker.getNextExpectedSplat(poisonState));
    }
}
