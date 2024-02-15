package com.poisonednpcs.combat;

import com.poisonednpcs.poison.PoisonType;
import junit.framework.TestCase;
import net.runelite.api.Hitsplat;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Integration tests for {@link HitTracker} and {@link PoisonTracker} tied together through {@link HealthStatus}.
 */
public class HealthStatusTest extends TestCase {

    public void testHitAndPoisonTrackersTiedTogether() throws InterruptedException {
        final Duration testDelay = Duration.ofMillis(5);
        final PoisonType poisonType = PoisonType.MELEE_PLUS_PLUS;
        HealthStatus.HIT_TO_POISON_DELAY = testDelay;

        AtomicBoolean isAlive = new AtomicBoolean(true);
        HealthStatus status = new HealthStatus(new HitTracker(isAlive::get));
        assertFalse(status.isPoisoned());
        assertFalse(status.isActive());

        status.getHitTracker().trackHit(new Hit(
                Optional.of(new Weapon(0, Optional.of(poisonType))),
                new Hitsplat(0, 1, 0)));
        assertFalse(status.isPoisoned());
        assertTrue(status.isActive());

        Thread.sleep(testDelay.multipliedBy(2).toMillis());
        assertFalse(status.getHitTracker().getOldestHitAfter(Instant.now().minus(testDelay)).isPresent());

        // The hit that will cause poison, now
        status.getHitTracker().trackHit(new Hit(
                Optional.of(new Weapon(0, Optional.of(poisonType))),
                new Hitsplat(0, 1, 0)));
        assertFalse(status.isPoisoned());
        assertTrue(status.isActive());

        for (int i = 0; i < poisonType.getProgression().length - 1; i++) {
            Thread.sleep(testDelay.toMillis());
            status.getPoisonTracker().registerPoisonSplat(new Hit(
                    Optional.empty(),
                    new Hitsplat(65, poisonType.getProgression()[i], 0)));
            assertTrue(status.isPoisoned());
            assertTrue(status.isActive());
        }

        // The last hit, which will register the kill and halt the poison
        Thread.sleep(testDelay.toMillis());
        status.getPoisonTracker().registerPoisonSplat(new Hit(
                Optional.empty(),
                new Hitsplat(65, poisonType.getProgression()[poisonType.getProgression().length - 1], 0)));
        assertFalse(status.isPoisoned());
        isAlive.set(false);
        assertFalse(status.isActive());
    }

}