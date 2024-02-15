package com.poisonednpcs.combat;

import com.google.common.annotations.VisibleForTesting;
import com.poisonednpcs.health.HitsplatType;
import com.poisonednpcs.poison.Poison;
import com.poisonednpcs.poison.PoisonType;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tracks and controls the progression of a poison sequence. Supports a "refresh" of the poison sequence in which
 * re-poisoning occurred at an undetermined point. Will continue to maintain the sequence and manage information
 * until the ambiguity removes itself, at which time it will resume the progression.
 */
@Slf4j
public class PoisonTracker {

    @VisibleForTesting
    static BiFunction<Optional<Hit>, Hit, PoisonType> getPoisonTypeFromHits = PoisonTracker::getPoisonType;

    private final Function<Hit, Optional<Hit>> getPoisiningHitFn;

    private Optional<PoisonState> current;

    /**
     * @param getPoisiningHitFn function which takes the first poison hit of the sequence and attempts to return the
     *                          hit which applied the poison.
     */
    public PoisonTracker(Function<Hit, Optional<Hit>> getPoisiningHitFn) {
        this.getPoisiningHitFn = getPoisiningHitFn;
        this.current = Optional.empty();
    }

    public void registerPoisonSplat(Hit hit) {
        if (HitsplatType.of(hit.getHitsplat().getHitsplatType()) != HitsplatType.OPPONENT_DAMAGED_BY_POISON) {
            throw new RuntimeException(String.format("hit is not a poisoning hit: %d", hit.getHitsplat().getHitsplatType()));
        }

        if (current.isEmpty()) {
            // new poison sequence, build a brand new status based on the amount of damage
            current = Optional.of(newPoisonSequence(hit));
        }

        // register that the poison splat happened -- this is intentional even though it returns no value
        current.get().splat();

        int amount = hit.getHitsplat().getAmount();
        int expectedNextDamage = current.get().nextExpectedDamage();
        // TODO: any way to do this sequence without if statements?
        if (amount == current.get().nextExpectedDamage()) {
            // everything matches expectations, proceed as normal
            current.get().nextStep();
        } else if (expectedNextDamage < amount - 1) {
            // This means the poison's max hit occurred, i.e., we restarted the sequence
            current = Optional.of(newPoisonSequence(hit));
            current.get().nextStep();
        } else if (expectedNextDamage < amount) {
            // we re-applied the poison but we don't know when! Hold position and introduce uncertainty
            current.get().markAmbiguous();
        } else {
            // This implies that the expected damage was GREATER than the amount that we saw, meaning our calculations
            // are off. Optimistically step and mark ambiguity, but this could indicate greater issues.
            current.get().nextStep();
            current.get().markAmbiguous();
        }

        // if we just applied our last piece of poison damage, kill the sequence altogether
        if (current.get().isFinished()) {
            current = Optional.empty();
        }
    }

    public Optional<PoisonState> getPoisonStatus() {
        return current;
    }

    public boolean isPoisoned() {
        return current.isPresent();
    }

    public static Instant getNextExpectedSplat(PoisonState poisonState) {
        return poisonState.getLastSplat().plus(Poison.POISON_DELAY);
    }

    private PoisonState newPoisonSequence(Hit poisonHit) {
        Optional<Hit> hitWhichCausedPoisoning = getPoisiningHitFn.apply(poisonHit);
        PoisonType poisonType = getPoisonTypeFromHits.apply(hitWhichCausedPoisoning, poisonHit);
        return new PoisonState(poisonType);
    }

    @VisibleForTesting
    static PoisonType getPoisonType(Optional<Hit> culpritHit, Hit poisonHit) {
        // defined here so that we can change as necessary during writing/debugging
        final Consumer<String> logFn = log::debug;

        logFn.accept(String.format("Culprit hit presence: %b", culpritHit.isPresent()));
        if (culpritHit.isPresent()) {
            Optional<Weapon> weapon = culpritHit.get().getWeapon();
            if (weapon.map(w -> w.getPoisonType().isPresent()).orElse(false)) {
                logFn.accept(String.format("Poison type detected from weapon: %s", weapon.get().getPoisonType()));
                return weapon.get().getPoisonType().get();
            }
        }

        // else we don't know the weapon that caused the poison, and we have to guess
        // RIGHT NOW, WE ALWAYS GUESS MELEE
        for (PoisonType poisonType : PoisonType.onlyMelee()) {
            // TODO: there's a danger here that we could mis-identify the poison type because (p++) and (kp) have the
            //  same progression; as is, that won't hurt anything, but if the poison type is displayed, it will be wrong
            if (poisonHit.getHitsplat().getAmount() == poisonType.maxHit()) {
                logFn.accept(String.format("Poison type detected from max hit: %s", poisonType));
                return poisonType;
            }
        }
        // when all else fails, we're not exactly hurting the situation by a conservativeish guess
        logFn.accept("Poison type defaulted");
        return PoisonType.MELEE;
    }
}
