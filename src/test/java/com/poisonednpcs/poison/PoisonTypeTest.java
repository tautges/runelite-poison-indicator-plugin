package com.poisonednpcs.poison;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;

import java.util.List;

/** Tests for {@link PoisonType}. */
public class PoisonTypeTest extends TestCase {

    public void testPoisonRegexMatches() {
        List<String> expectedMatches = ImmutableList.of(
                "Iron dagger(p)",
                "Rune dagger(p)",
                "Iron arrow(p)",
                "Dragon arrow(p)");

        for (String expectedMatch : expectedMatches) {
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.MELEE.getWeaponRegex().matcher(expectedMatch).find());
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.RANGED.getWeaponRegex().matcher(expectedMatch).find());
        }

        List<String> expectedMisses = ImmutableList.of(
                "Iron dagger(p)e",
                "Iron dagger(p",
                "Iron dagger(p+)",
                "Rune dagger(p++)",
                "Iron arrow(p++)",
                "Bronze kiteshield",
                "Dragon arrow");

        for (String expectedMiss : expectedMisses) {
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.MELEE.getWeaponRegex().matcher(expectedMiss).find());
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.RANGED.getWeaponRegex().matcher(expectedMiss).find());
        }
    }

    public void testPoisonPlusRegexMatches() {
        List<String> expectedMatches = ImmutableList.of(
                "Iron dagger(p+)",
                "Rune dagger(p+)",
                "Iron arrow(p+)",
                "Dragon arrow(p+)");

        for (String expectedMatch : expectedMatches) {
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.MELEE_PLUS.getWeaponRegex().matcher(expectedMatch).find());
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.RANGED_PLUS.getWeaponRegex().matcher(expectedMatch).find());
        }

        List<String> expectedMisses = ImmutableList.of(
                "Iron dagger(p)e",
                "Iron dagger(p",
                "Iron dagger(p)",
                "Rune dagger(p++)",
                "Iron arrow(p++)",
                "Bronze kiteshield",
                "Dragon arrow");

        for (String expectedMiss : expectedMisses) {
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.MELEE_PLUS.getWeaponRegex().matcher(expectedMiss).find());
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.RANGED_PLUS.getWeaponRegex().matcher(expectedMiss).find());
        }
    }

    public void testPoisonPlusPlusRegexMatches() {
        List<String> expectedMatches = ImmutableList.of(
                "Iron dagger(p++)",
                "Rune dagger(p++)",
                "Iron arrow(p++)",
                "Dragon arrow(p++)");

        for (String expectedMatch : expectedMatches) {
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.MELEE_PLUS_PLUS.getWeaponRegex().matcher(expectedMatch).find());
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.RANGED_PLUS_PLUS.getWeaponRegex().matcher(expectedMatch).find());
        }

        List<String> expectedMisses = ImmutableList.of(
                "Iron dagger(p)e",
                "Iron dagger(p",
                "Iron dagger(p)",
                "Rune dagger(p+)",
                "Iron arrow(p+)",
                "Bronze kiteshield",
                "Dragon arrow");

        for (String expectedMiss : expectedMisses) {
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.MELEE_PLUS_PLUS.getWeaponRegex().matcher(expectedMiss).find());
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.RANGED_PLUS_PLUS.getWeaponRegex().matcher(expectedMiss).find());
        }
    }

    public void testKarambwanRegexMatches() {
        List<String> expectedMatches = ImmutableList.of(
                "Bronze spear(kp)",
                "Rune spear(kp)",
                "Rune hasta(kp)",
                "Dragon hasta(kp)");

        for (String expectedMatch : expectedMatches) {
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.KARAMBWAN.getWeaponRegex().matcher(expectedMatch).find());
        }

        List<String> expectedMisses = ImmutableList.of(
                "Bronze spear",
                "Bronze spear(p)",
                "Iron dagger(p)",
                "Rune dagger(p+)",
                "Iron arrow(p++)",
                "Rune spear",
                "Dragon hasta");

        for (String expectedMiss : expectedMisses) {
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.KARAMBWAN.getWeaponRegex().matcher(expectedMiss).find());
        }
    }

    public void testEmeraldBoltsERegexMatches() {
        List<String> expectedMatches = ImmutableList.of(
                "Emerald bolts (e)");


        for (String expectedMatch : expectedMatches) {
            assertTrue(String.format("expected match on %s", expectedMatch),
                    PoisonType.EMERALD_BOLTS_E.getWeaponRegex().matcher(expectedMatch).find());
        }

        List<String> expectedMisses = ImmutableList.of(
                "Bronze bolts (e)",
                "Emerald bolts(e)",
                "Emerald bolts (e",
                "Emerald bolts e)",
                "Ruby bolts (e)",
                "Diamond bolts (e)",
                "ahhhhhhhhhhhhhh");

        for (String expectedMiss : expectedMisses) {
            assertFalse(String.format("expected miss on %s", expectedMiss),
                    PoisonType.EMERALD_BOLTS_E.getWeaponRegex().matcher(expectedMiss).find());
        }
    }

}