package com.poisonednpcs.npcs;

import junit.framework.TestCase;

/** Tests for {@link OpponentCuller}. */
public class OpponentCullerTest extends TestCase {

    public void testNoConditionsMatching() {
        Opponent opponent = new Opponent(null, 1);

        OpponentCuller culler = OpponentCuller.newBuilder()
                .add(o -> o.getNPC() != null)
                .add(o -> o.getMaxHealth() == 3)
                .build();

        assertEquals(false, culler.test(opponent));
    }

    public void testAnyConditionMatches() {
        Opponent opponent = new Opponent(null, 1);

        OpponentCuller culler = OpponentCuller.newBuilder()
                .add(o -> o.getNPC() != null)
                .add(o -> o.getMaxHealth() == 1)
                .build();

        assertEquals(true, culler.test(opponent));
    }

    public void testAllConditionsMatching() {
        Opponent opponent = new Opponent(null, 1);

        OpponentCuller culler = OpponentCuller.newBuilder()
                .add(o -> o.getNPC() == null)
                .add(o -> o.getMaxHealth() == 3)
                .build();

        assertEquals(true, culler.test(opponent));
    }

}