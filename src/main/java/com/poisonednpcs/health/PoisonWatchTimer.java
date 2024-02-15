package com.poisonednpcs.health;

import com.poisonednpcs.combat.PoisonState;
import com.poisonednpcs.npcs.NPCUtils;
import com.poisonednpcs.npcs.Opponent;
import com.poisonednpcs.npcs.OpponentCuller;
import com.poisonednpcs.util.DurationUtils;
import com.poisonednpcs.util.Multiline;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.function.Predicate;

/**
 * Defines an info box which displays relevant information about ongoing possibly-poisoned combat with a single NPC.
 * The live timer value will either indicate:
 *  a) the next possible time at which poison could begin to occur, if the NPC is not already poisoned
 *  b) the next time at which poison will splat on the NPC, iff the NPC is poisoned
 *
 * All information regarding poison is retrieved from the opponent specified to this timer at construction time.
 */
public class PoisonWatchTimer extends InfoBox {

    private final Opponent opponent;

    public PoisonWatchTimer(Opponent opponent, BufferedImage image, Plugin plugin) {
        super(image, plugin);
        this.opponent = opponent;
    }

    @Override
    public String getText() {
        Duration toShow;
        if (opponent.getHealthStatus().isPoisoned()) {
            toShow = opponent.getHealthStatus().getPoisonTracker().getPoisonStatus()
                    .map(Tracking::timeUntilPoisonSplat).orElse(Duration.ZERO);
        } else {
            toShow = Tracking.timeUntilPossiblePoison(opponent.getHealthStatus().getHitTracker());
        }
        return DurationUtils.durationToSecondsString(toShow);
    }

    @Override
    public Color getTextColor() {
        return opponent.getHealthStatus().isPoisoned() ? Color.GREEN : Color.YELLOW;
    }

    @Override
    public boolean cull() {
        return !opponent.getHealthStatus().isActive() || opponent.getNPC().isDead();
    }

    /**
     * @return true iff the specified opponent equals the opponent belonging to the timer
     */
    public boolean isForOpponent(Opponent opponent) {
        return NPCUtils.getIdentifier(this.opponent.getNPC()) == NPCUtils.getIdentifier(opponent.getNPC());
    }

    /**
     * Defines a dead predicate which will always return false but will also update any tooltips for
     * {@link PoisonWatchTimer}s whose opponents match the specified opponent. We do this like so in order to avoid
     * needing to maintain outside references to timers which need later tooltip updates; maintain those references
     * would interfere with garbage collection after they have been removed from the info box management.
     *
     * @return a predicate which returns false under all circumstances and updates the tooltip for any timers for this opponent
     */
    public static Predicate<InfoBox> updateTooltip(Opponent opponent) {
        return infoBox -> {
            if (!(infoBox instanceof PoisonWatchTimer)) {
                // Not a thing we need to update, leave early
                return false;
            }
            PoisonWatchTimer poisonWatchTimer = (PoisonWatchTimer) infoBox;
            if (!poisonWatchTimer.isForOpponent(opponent)) {
                // We don't care about this timer, as it's not for the correct opponent, skip anything else
                return false;
            }

            if (opponent.getHealthStatus().isPoisoned()) {
                PoisonState poisonState = opponent.getHealthStatus().getPoisonTracker().getPoisonStatus().get();
                int remainingHealth = opponent.getRemainingHealth();

                poisonWatchTimer.setTooltip(new Multiline()
                        .append(String.format("%s is poisoned", opponent.getNPC().getName()))
                        .append(String.format("Next poison damage: %d", poisonState.nextExpectedDamage()))
                        .append(String.format("Remaining poison damage: %d", poisonState.getDamageRemaining()))
                        .append(String.format("Remaining opponent health: %s", remainingHealth < 0 ? "??" : String.format("~%d", remainingHealth)))
                        .toString());
            } else if (opponent.getHealthStatus().isActive()) {
                // This means we're getting hit but not poisoned, show the next poison opportunity
                poisonWatchTimer.setTooltip(
                        String.format("Next opportunity for %s to be poisoned", opponent.getNPC().getName()));
            }

            // THIS MUST ALWAYS BE FALSE! We are intentionally preventing the info box from being removed through here.
            return false;
        };
    }

    public static Predicate<InfoBox> cullOpponents(OpponentCuller opponentCuller) {
        return infoBox -> {
            if (!(infoBox instanceof PoisonWatchTimer)) {
                // Not a thing we need to update, leave early
                return false;
            }
            PoisonWatchTimer poisonWatchTimer = (PoisonWatchTimer) infoBox;
            return opponentCuller.test(poisonWatchTimer.opponent);
        };
    }
}
