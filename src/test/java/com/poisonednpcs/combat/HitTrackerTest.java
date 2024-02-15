package com.poisonednpcs.combat;

import com.poisonednpcs.poison.PoisonType;
import junit.framework.TestCase;
import net.runelite.api.Hitsplat;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** Tests for {@link HitTracker}. */
public class HitTrackerTest extends TestCase {

    public void testIgnoreNonPoisonHit() {
        final int amount = 5;

        Instant starting = Instant.now();

        HitTracker tracker = new HitTracker(() -> true);
        assertFalse(tracker.hasHits());

        Hit hit = new Hit(Optional.of(new Weapon(0, Optional.empty())), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit);
        assertFalse(tracker.hasHits());

        Optional<Hit> retrieved = tracker.getOldestHitAfter(starting);
        assertFalse(retrieved.isPresent());
    }

    public void testTrackPoisonHit() {
        final int amount = 5;

        Instant starting = Instant.now();

        HitTracker tracker = new HitTracker(() -> true);
        assertFalse(tracker.hasHits());

        Hit hit = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit);
        assertTrue(tracker.hasHits());

        Optional<Hit> retrieved = tracker.getOldestHitAfter(starting);
        assertTrue(retrieved.isPresent());
        assertEquals(hit, retrieved.get());
    }

    public void testHasHitsViaIsActive() {
        final int value = 0;
        final int amount = 5;

        AtomicInteger test = new AtomicInteger(value);

        HitTracker tracker = new HitTracker(() -> test.get() == value);
        assertFalse(tracker.hasHits());

        Hit hit = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit);
        assertTrue(tracker.hasHits());

        test.set(value + 1);
        assertFalse(tracker.hasHits());
    }

    public void testGetOldestHitAfter() {
        final int amount = 5;

        HitTracker tracker = new HitTracker(() -> true);
        assertFalse(tracker.hasHits());

        Hit hit1 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount - 1, 500));
        tracker.trackHit(hit1);
        assertTrue(tracker.hasHits());

        Instant starting = Instant.now();

        Hit hit2 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit2);
        assertTrue(tracker.hasHits());
        Hit hit3 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount + 2, 1500));
        tracker.trackHit(hit3);
        assertTrue(tracker.hasHits());

        Optional<Hit> retrieved = tracker.getOldestHitAfter(starting);
        assertTrue(retrieved.isPresent());
        assertEquals(hit2, retrieved.get());
    }

    public void testGetClosestTrackedHitToOccurredAfter() throws InterruptedException {
        final int amount = 5;
        HitTracker tracker = new HitTracker(() -> true);

        Hit hit1 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount - 1, 500));
        tracker.trackHit(hit1);
        assertTrue(tracker.hasHits());
        Thread.sleep(30);

        Instant pullTime = Instant.now();
        Thread.sleep(20);

        Hit hit2 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit2);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        Hit hit3 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount + 2, 1500));
        tracker.trackHit(hit3);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        tracker.getOldestHitAfter(pullTime);

        Optional<Hit> closest = tracker.getClosestTrackedHitTo(pullTime, Duration.ofMillis(50));
        assertTrue(closest.isPresent());
        assertEquals(hit2, closest.get());
    }

    public void testGetClosestTrackedHitToOccurredBefore() throws InterruptedException {
        final int amount = 5;
        HitTracker tracker = new HitTracker(() -> true);

        Hit hit1 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount - 1, 500));
        tracker.trackHit(hit1);
        assertTrue(tracker.hasHits());
        Thread.sleep(20);

        Instant pullTime = Instant.now();
        Thread.sleep(30);

        Hit hit2 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit2);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        Hit hit3 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount + 2, 1500));
        tracker.trackHit(hit3);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        tracker.getOldestHitAfter(pullTime);

        Optional<Hit> closest = tracker.getClosestTrackedHitTo(pullTime, Duration.ofMillis(50));
        assertTrue(closest.isPresent());
        assertEquals(hit1, closest.get());
    }

    public void testGetClosestTrackedHitToNoPreviousHit() throws InterruptedException {
        final int amount = 5;
        HitTracker tracker = new HitTracker(() -> true);

        Instant pullTime = Instant.now();
        Thread.sleep(30);

        Hit hit2 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit2);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        Hit hit3 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount + 2, 1500));
        tracker.trackHit(hit3);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        tracker.getOldestHitAfter(pullTime);

        Optional<Hit> closest = tracker.getClosestTrackedHitTo(pullTime, Duration.ofMillis(50));
        assertTrue(closest.isPresent());
        assertEquals(hit2, closest.get());
    }

    public void testGetClosestTrackedHitToNoAfterHit() throws InterruptedException {
        final int amount = 5;
        HitTracker tracker = new HitTracker(() -> true);

        Hit hit2 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit2);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        Hit hit3 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount + 2, 1500));
        tracker.trackHit(hit3);
        assertTrue(tracker.hasHits());
        Thread.sleep(20);

        Instant pullTime = Instant.now();

        tracker.getOldestHitAfter(pullTime);

        Optional<Hit> closest = tracker.getClosestTrackedHitTo(pullTime, Duration.ofMillis(50));
        assertTrue(closest.isPresent());
        assertEquals(hit3, closest.get());
    }

    public void testGetClosestTrackedHitToOutsideGracePeriod() throws InterruptedException {
        final int amount = 5;
        HitTracker tracker = new HitTracker(() -> true);

        Hit hit1 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount - 1, 500));
        tracker.trackHit(hit1);
        assertTrue(tracker.hasHits());
        Thread.sleep(20);

        Instant pullTime = Instant.now();
        Thread.sleep(30);

        Hit hit2 = new Hit(Optional.of(new Weapon(0, Optional.of(PoisonType.MELEE))), new Hitsplat(0, amount, 1000));
        tracker.trackHit(hit2);
        assertTrue(tracker.hasHits());
        Thread.sleep(50);

        tracker.getOldestHitAfter(pullTime);

        // narrow grace period!
        Optional<Hit> closest = tracker.getClosestTrackedHitTo(pullTime, Duration.ofMillis(10));
        assertFalse(closest.isPresent());
    }

}