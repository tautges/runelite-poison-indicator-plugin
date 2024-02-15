package com.poisonednpcs;

import com.poisonednpcs.combat.HealthStatus;
import com.poisonednpcs.combat.PoisonTracker;
import com.poisonednpcs.health.Tracking;
import com.poisonednpcs.npcs.NPCTrackingService;
import com.poisonednpcs.npcs.Opponent;
import com.poisonednpcs.util.DurationUtils;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;

public class PoisonedNPCsOverlay extends Overlay {

    private final NPCTrackingService npcTrackingService;
    private final PoisonedNPCsConfig config;

    @Inject
    private PoisonedNPCsOverlay(NPCTrackingService npcTrackingService, PoisonedNPCsConfig config) {
        this.npcTrackingService = npcTrackingService;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.displayNPCPoisonOverlay()) {
            npcTrackingService.forEachCombat(opponent -> renderOverlay(graphics, opponent));
        }
        return null;
    }

    private void renderOverlay(Graphics2D graphics, Opponent opponent) {
        boolean isPoisoned = opponent.getHealthStatus().isPoisoned();

        Color color = isPoisoned ? Color.GREEN : Color.YELLOW;
        String myText;
        if (!isPoisoned) {
            myText = String.format("%s", DurationUtils.durationToSecondsString(
                    Tracking.timeUntilPossiblePoison(opponent.getHealthStatus().getHitTracker())));
        } else {
            myText = getPoisonNotificationString(opponent.getHealthStatus());
        }

        if (!myText.isEmpty()) {
            int zOffset = opponent.getNPC().getLogicalHeight() + 40;
            Point location = opponent.getNPC().getCanvasTextLocation(graphics, "", zOffset);
            if (location != null && location.getX() > 0 && location.getY() > 0) {
                // adjust the x coordinate by the width of the string
                location = new Point(location.getX() - graphics.getFontMetrics().stringWidth(myText) / 2, location.getY());
                OverlayUtil.renderTextLocation(graphics, location, myText, color);
            }
        }
    }

    private String getPoisonNotificationString(HealthStatus status) {
        PoisonTracker poisonTracker = status.getPoisonTracker();
        return poisonTracker.getPoisonStatus()
                .map(poisonStatus -> {
                    Duration untilNextSplat = Tracking.timeUntilPoisonSplat(poisonStatus);
                    boolean isAmbiguous = poisonStatus.isAmbiguous();

                    StringBuilder output = new StringBuilder(DurationUtils.durationToSecondsString(untilNextSplat));
                    if (config.displayNPCOverlayNextDamage()) {
                        int expectedDamage = poisonStatus.nextExpectedDamage();
                        output.append(" ").append(String.format("(%d%s)", expectedDamage, isAmbiguous ? "+" : ""));
                    }
                    if (config.displayNPCOverlayTotalDamage()) {
                        int damageRemaining = poisonStatus.getDamageRemaining();
                        output.append(" ").append(String.format("[%s%d]", isAmbiguous ? ">=" : "", damageRemaining));
                    }
                    return output.toString();
                }).orElse("");
    }
}
