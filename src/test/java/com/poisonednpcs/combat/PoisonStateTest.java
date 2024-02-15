package com.poisonednpcs.combat;

import com.poisonednpcs.poison.PoisonType;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.Arrays;

/** Tests for {@link PoisonState}. */
public class PoisonStateTest extends TestCase {

    public void testMarkAmbiguous() {
        PoisonState state = new PoisonState(PoisonType.MELEE);
        assertFalse(state.isAmbiguous());
        state.markAmbiguous();
        assertTrue(state.isAmbiguous());
    }

    public void testNextStep() {
        final PoisonType type = PoisonType.MELEE;

        PoisonState state = new PoisonState(type);
        assertFalse(state.isFinished());
        assertNull(state.getLastSplat());

        int next = state.nextExpectedDamage();
        assertEquals(type.getProgression()[0], next);

        int damageRemaining = state.getDamageRemaining();
        int expectedDamageRemaining = Arrays.stream(type.getProgression()).sum();
        assertEquals(expectedDamageRemaining, damageRemaining);

        state.nextStep();
        assertEquals(damageRemaining - next, state.getDamageRemaining());
    }

    public void testNextStepClearsAmbiguity() {
        final PoisonType type = PoisonType.MELEE;

        PoisonState state = new PoisonState(type);
        assertFalse(state.isAmbiguous());

        state.markAmbiguous();
        assertTrue(state.isAmbiguous());

        state.nextStep();
        assertFalse(state.isAmbiguous());
    }

    public void testFullProgression() {
        final PoisonType type = PoisonType.MELEE;

        PoisonState state = new PoisonState(type);
        assertFalse(state.isFinished());
        assertNull(state.getLastSplat());

        int expectedDamageRemaining = Arrays.stream(type.getProgression()).sum();

        int[] progression = type.getProgression();
        for (int next : progression) {
            assertFalse(state.isFinished());
            int actualDamageRemaining = state.getDamageRemaining();
            assertEquals(expectedDamageRemaining, actualDamageRemaining);
            int nextExpected = state.nextExpectedDamage();
            assertEquals(next, nextExpected);

            state.nextStep();
            expectedDamageRemaining -= nextExpected;
        }
        assertTrue(state.isFinished());
        assertEquals(0, state.nextExpectedDamage());
        assertEquals(0, expectedDamageRemaining);
    }

    public void testSplat() {
        final PoisonType type = PoisonType.MELEE;
        PoisonState state = new PoisonState(type);
        assertNull(state.getLastSplat());
        Instant before = Instant.now();
        state.splat();
        Instant after = Instant.now();

        Instant lastSplat = state.getLastSplat();
        assertNotNull(lastSplat);
        assertTrue(before.isBefore(lastSplat));
        assertTrue(after.isAfter(lastSplat));
    }
}