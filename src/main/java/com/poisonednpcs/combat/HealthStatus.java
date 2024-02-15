package com.poisonednpcs.combat;

import com.google.common.annotations.VisibleForTesting;
import com.poisonednpcs.poison.Poison;
import lombok.Getter;

import java.time.Duration;

@Getter
public class HealthStatus {

    @VisibleForTesting
    static Duration HIT_TO_POISON_DELAY = Poison.POISON_DELAY;

    /** Grace period that we allow when examining if hits occurred at certain times, here defined as a game tick. */
    private static final Duration HIT_TRACKING_GRACE_PERIOD = Poison.GAME_TICK;

    private final PoisonTracker poisonTracker;
    private final HitTracker hitTracker;

    // TODO: do we really need to pass the HitTracker in?
    public HealthStatus(HitTracker hitTracker) {
        this.hitTracker = hitTracker;
        this.poisonTracker = new PoisonTracker(poisonHit -> hitTracker.getClosestTrackedHitTo(
                poisonHit.getOccurredAt().minus(HIT_TO_POISON_DELAY),
                HIT_TRACKING_GRACE_PERIOD));
    }

    public boolean isActive() {
        return hitTracker.hasHits() || isPoisoned();
    }

    public boolean isPoisoned() {
        return poisonTracker.isPoisoned();
    }
}
