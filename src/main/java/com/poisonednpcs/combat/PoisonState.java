package com.poisonednpcs.combat;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.poisonednpcs.poison.PoisonType;
import lombok.Getter;

import java.time.Instant;
import java.util.Arrays;

/**
 * Records and controls the state of a poison progression, including the poison damage which has yet to occur within
 * the progression, the next expected poison splat, and the time at which the last poison splat occurred.
 */
public class PoisonState {

    private final PeekingIterator<Integer> progression;

    @Getter
    private int damageRemaining;
    private boolean isInAmbiguousState = false;
    private Instant lastSplat;

    PoisonState(PoisonType poisonType) {
        this.progression = Iterators.peekingIterator(Arrays.stream(poisonType.getProgression()).iterator());
        this.damageRemaining = Arrays.stream(poisonType.getProgression()).sum();
    }

    public boolean isAmbiguous() {
        return isInAmbiguousState;
    }

    public int nextExpectedDamage() {
        return isFinished() ? 0 : progression.peek();
    }

    int nextStep() {
        damageRemaining -= progression.peek();
        // once we step, we have a definitive place in the poison progression again and are no longer ambiguous
        isInAmbiguousState = false;
        return isFinished() ? 0 : progression.next();
    }

    boolean isFinished() {
        return !progression.hasNext();
    }

    void splat() {
        lastSplat = Instant.now();
    }

    Instant getLastSplat() {
        return lastSplat;
    }

    void markAmbiguous() {
        isInAmbiguousState = true;
    }
}
