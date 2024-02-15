package com.poisonednpcs.health;

import com.poisonednpcs.combat.HealthStatus;
import com.poisonednpcs.mocks.MockNPC;
import com.poisonednpcs.npcs.Opponent;
import com.poisonednpcs.npcs.OpponentCuller;
import junit.framework.TestCase;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import org.mockito.Mockito;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class PoisonWatchTimerTest extends TestCase {

    public void testGetTextColor() {
        final int id = 1;

        MockNPC mockNPC = MockNPC.newSetup().getIndexFn(() -> id).get();
        HealthStatus mockStatus = Mockito.mock(HealthStatus.class);
        Mockito.when(mockStatus.isPoisoned()).thenReturn(true).thenReturn(false);
        Opponent mockOpponent = Mockito.mock(Opponent.class);
        Mockito.when(mockOpponent.getNPC()).thenReturn(mockNPC);
        Mockito.when(mockOpponent.getHealthStatus()).thenReturn(mockStatus);

        PoisonWatchTimer timer = new PoisonWatchTimer(mockOpponent, null, null);

        assertEquals(Color.GREEN, timer.getTextColor());
        assertEquals(Color.YELLOW, timer.getTextColor());
    }

    public void testCull() {
        AtomicBoolean isDead = new AtomicBoolean(false);

        MockNPC mockNPC = MockNPC.newSetup().setIsDeadFn(isDead::get).get();
        HealthStatus mockStatus = Mockito.mock(HealthStatus.class);
        Mockito.when(mockStatus.isActive())
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(true)
                .thenReturn(false);

        Opponent mockOpponent = Mockito.mock(Opponent.class);
        Mockito.when(mockOpponent.getNPC()).thenReturn(mockNPC);
        Mockito.when(mockOpponent.getHealthStatus()).thenReturn(mockStatus);

        PoisonWatchTimer timer = new PoisonWatchTimer(mockOpponent, null, null);

        assertFalse(timer.cull());
        assertTrue(timer.cull());
        isDead.set(true);
        assertTrue(timer.cull());
        assertTrue(timer.cull());
    }

    public void testUpdateTooltip() {
        final int id1 = 1;
        final int id2 = 2;

        MockNPC mockNPC1 = MockNPC.newSetup().getIndexFn(() -> id1).get();
        MockNPC mockNPC2 = MockNPC.newSetup().getIndexFn(() -> id2).get();

        HealthStatus status = Mockito.mock(HealthStatus.class);
        Mockito.when(status.isActive()).thenReturn(true);

        Opponent opponent1 = Mockito.mock(Opponent.class);
        Mockito.when(opponent1.getNPC()).thenReturn(mockNPC1);
        Mockito.when(opponent1.getHealthStatus()).thenReturn(status);
        Opponent opponent2 = Mockito.mock(Opponent.class);
        Mockito.when(opponent2.getNPC()).thenReturn(mockNPC2);
        Mockito.when(opponent2.getHealthStatus()).thenReturn(status);

        Predicate<InfoBox> updateTooltip = PoisonWatchTimer.updateTooltip(opponent1);

        PoisonWatchTimer timer1 = new PoisonWatchTimer(opponent1, null, null);
        String tooltip1 = timer1.getTooltip();
        PoisonWatchTimer timer2 = new PoisonWatchTimer(opponent2, null, null);
        String tooltip2 = timer2.getTooltip();

        assertFalse(updateTooltip.test(timer1));
        assertFalse(updateTooltip.test(timer2));
        assertNotSame(tooltip1, timer1.getTooltip());
        assertSame(tooltip2, timer2.getTooltip());
    }

    public void testCullOpponents() {
        final int id1 = 1;
        final int id2 = 2;

        MockNPC mockNPC1 = MockNPC.newSetup().getIndexFn(() -> id1).get();
        MockNPC mockNPC2 = MockNPC.newSetup().getIndexFn(() -> id2).get();

        HealthStatus status = Mockito.mock(HealthStatus.class);
        Mockito.when(status.isActive()).thenReturn(true);

        Opponent opponent1 = Mockito.mock(Opponent.class);
        Mockito.when(opponent1.getNPC()).thenReturn(mockNPC1);
        Mockito.when(opponent1.getHealthStatus()).thenReturn(status);
        Opponent opponent2 = Mockito.mock(Opponent.class);
        Mockito.when(opponent2.getNPC()).thenReturn(mockNPC2);
        Mockito.when(opponent2.getHealthStatus()).thenReturn(status);

        Predicate<InfoBox> updateTooltip = PoisonWatchTimer.cullOpponents(OpponentCuller.newBuilder()
                .add(opponent -> opponent.getNPC().getIndex() == id1)
                .build());

        PoisonWatchTimer timer1 = new PoisonWatchTimer(opponent1, null, null);
        PoisonWatchTimer timer2 = new PoisonWatchTimer(opponent2, null, null);

        assertTrue(updateTooltip.test(timer1));
        assertFalse(updateTooltip.test(timer2));
    }
}