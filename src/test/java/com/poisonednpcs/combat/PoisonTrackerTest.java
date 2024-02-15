package com.poisonednpcs.combat;

import com.poisonednpcs.poison.PoisonType;
import junit.framework.TestCase;
import net.runelite.api.Hitsplat;
import org.junit.Assert;

import java.util.Optional;

public class PoisonTrackerTest extends TestCase {

    public void testRegisterBadSplat() {
        PoisonTracker tracker = new PoisonTracker(poisonHit -> Optional.empty());

        try {
            tracker.registerPoisonSplat(new Hit(Optional.empty(), new Hitsplat(1, 1, 0)));
            Assert.fail("no exception was thrown on bad hitsplat");
        } catch (RuntimeException ex) {}
    }

    public void testRegisterGoodSplat() {
        final PoisonType type = PoisonType.MELEE;
        PoisonTracker tracker = new PoisonTracker(poisonHit -> Optional.empty());

        assertFalse(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.isPoisoned());

        Hit poisonHit = new Hit(Optional.empty(), new Hitsplat(65, type.getProgression()[0], 0));

        // Java frowns on this but if we're only doing it in testing, let it be so.
        PoisonTracker.getPoisonTypeFromHits = (culpritHit, _poisonHit) -> {
            assertEquals(poisonHit, _poisonHit);
            return type;
        };

        tracker.registerPoisonSplat(poisonHit);
        assertTrue(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.getPoisonStatus().get().isAmbiguous());
        assertTrue(tracker.isPoisoned());
    }

    public void testRegisterRestartSplat() {
        final PoisonType type = PoisonType.MELEE;
        PoisonTracker tracker = new PoisonTracker(poisonHit -> Optional.empty());

        assertFalse(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.isPoisoned());

        Hit poisonHit = new Hit(Optional.empty(), new Hitsplat(65, type.getProgression()[0], 0));

        PoisonTracker.getPoisonTypeFromHits = (culpritHit, _poisonHit) -> type;

        for (int i = 0; type.getProgression()[i] >= type.getProgression()[0] - 1; i++) {
            tracker.registerPoisonSplat(new Hit(Optional.empty(), new Hitsplat(65, type.getProgression()[i], 0)));
            assertFalse(tracker.getPoisonStatus().get().isAmbiguous());
        }

        tracker.registerPoisonSplat(poisonHit);
        assertTrue(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.getPoisonStatus().get().isAmbiguous());
        assertTrue(tracker.isPoisoned());
    }

    public void testRegisterAmbiguousSplat() {
        final PoisonType type = PoisonType.MELEE;
        PoisonTracker tracker = new PoisonTracker(poisonHit -> Optional.empty());

        assertFalse(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.isPoisoned());

        Hit poisonHit = new Hit(Optional.empty(), new Hitsplat(65, type.getProgression()[0], 0));

        PoisonTracker.getPoisonTypeFromHits = (culpritHit, _poisonHit) -> type;

        for (int i = 0; type.getProgression()[i] == type.getProgression()[0]; i++) {
            tracker.registerPoisonSplat(poisonHit);
            assertFalse(tracker.getPoisonStatus().get().isAmbiguous());
        }

        tracker.registerPoisonSplat(poisonHit);
        assertTrue(tracker.getPoisonStatus().isPresent());
        assertTrue(tracker.getPoisonStatus().get().isAmbiguous());
        assertTrue(tracker.isPoisoned());
    }

    public void testRegisterFullSequence() {
        final PoisonType type = PoisonType.MELEE;
        PoisonTracker tracker = new PoisonTracker(poisonHit -> Optional.empty());

        assertFalse(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.isPoisoned());

        PoisonTracker.getPoisonTypeFromHits = (culpritHit, _poisonHit) -> type;

        for (int i = 0; i < type.getProgression().length - 1; i++) {
            tracker.registerPoisonSplat(new Hit(Optional.empty(), new Hitsplat(65, type.getProgression()[i], 0)));
            assertFalse(tracker.getPoisonStatus().get().isAmbiguous());
        }

        tracker.registerPoisonSplat(new Hit(Optional.empty(), new Hitsplat(65, type.getProgression()[type.getProgression().length - 1], 0)));
        assertFalse(tracker.getPoisonStatus().isPresent());
        assertFalse(tracker.isPoisoned());
    }

    public void testGetPoisonTypeFromWeapon() {
        final PoisonType expectedType = PoisonType.RANGED_PLUS;
        PoisonType determinedType = PoisonTracker.getPoisonType(
                Optional.of(new Hit(Optional.of(new Weapon(0, Optional.of(expectedType))), null)),
                new Hit(Optional.empty(), new Hitsplat(0, 3, 0)));
        assertEquals(expectedType, determinedType);
    }

    public void testGetPoisonTypeByMaxHit() {
        final PoisonType expectedType = PoisonType.MELEE_PLUS;
        final int maxDamage = PoisonType.MELEE_PLUS.maxHit();
        PoisonType determinedType = PoisonTracker.getPoisonType(
                Optional.of(new Hit(Optional.of(new Weapon(0, Optional.empty())), null)),
                new Hit(Optional.empty(), new Hitsplat(0, maxDamage, 0)));
        assertEquals(expectedType, determinedType);
    }

    public void testGetDefaultPoisonType() {
        PoisonType determinedType = PoisonTracker.getPoisonType(
                Optional.of(new Hit(Optional.of(new Weapon(0, Optional.empty())), null)),
                new Hit(Optional.empty(), new Hitsplat(0, 3, 0)));
        assertEquals(PoisonType.MELEE, determinedType);
    }
}