package com.poisonednpcs;

import com.poisonednpcs.combat.HealthStatus;
import com.poisonednpcs.combat.PoisonTracker;
import com.poisonednpcs.health.Tracking;
import com.poisonednpcs.npcs.NPCTrackingService;
import com.poisonednpcs.npcs.Opponent;
import com.poisonednpcs.util.DurationUtils;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            // Create a new renderer to do all overlays within this cycle
            PoisonOverlayRenderer overlayRenderer = new PoisonOverlayRenderer(config, graphics);
            // We only render overlays for NPCs with which we are in combat
            npcTrackingService.forEachCombat(overlayRenderer::render);
        }
        return null;
    }

    // TODO: could be an OverlayRenderer interface?
    // TODO: make visible to test? Seems prudent, if I can mock everything I need
    private static class PoisonOverlayRenderer {

        /**
         * The offset to add to a text position's Y value to separate it from other values located at the same
         * position. The value can be thought of as "the height of the text" + "a reasonable vertical margin between
         * texts being displayed".
         */
        private static final int STACKED_OVERLAYS_OFFSET = 15;

        private final PoisonedNPCsConfig config;
        private final Graphics2D graphics;
        private final Map<WorldPoint, Integer> worldPointCounters = new HashMap<>();

        PoisonOverlayRenderer(PoisonedNPCsConfig config, Graphics2D graphics) {
            this.config = config;
            this.graphics = graphics;
        }

        void render(Opponent opponent) {
            WorldPoint worldLocation = opponent.getNPC().getWorldLocation();
            // Increment the number of NPCs we see at this world location. We'll use this to determine the physical
            // Y value (virtual Z value) of the poison overlay.
            int occupantCount = worldPointCounters.merge(worldLocation, 1, Integer::sum);

            boolean isPoisoned = opponent.getHealthStatus().isPoisoned();

            Color color = isPoisoned ? Color.GREEN : Color.YELLOW;
            Optional<String> overlayText = Optional.empty();
            if (!isPoisoned) {
                Duration untilPossiblePoison = Tracking.timeUntilPossiblePoison(opponent.getHealthStatus().getHitTracker());
                if (untilPossiblePoison != Duration.ZERO) {
                    overlayText = Optional.of(DurationUtils.durationToSecondsString(untilPossiblePoison));
                }
            } else {
                overlayText = getPoisonNotificationString(opponent.getHealthStatus());
            }

            overlayText.ifPresent(text -> {
                int zOffset = opponent.getNPC().getLogicalHeight() + 40;
                Point location = opponent.getNPC().getCanvasTextLocation(graphics, "", zOffset);
                if (location != null && location.getX() > 0 && location.getY() > 0) {
                    location = new Point(
                        // adjust the x coordinate by the width of the string
                        location.getX() - graphics.getFontMetrics().stringWidth(text) / 2,
                        // We want the text to appear overhead, but we also need to separate the overlays for NPCs which are
                        // occupying the same tile. So stack them (using the occupantCount) as we cycle over them.
                        location.getY() - STACKED_OVERLAYS_OFFSET * (occupantCount - 1));
                    OverlayUtil.renderTextLocation(graphics, location, text, color);
                }
            });
        }

        private Optional<String> getPoisonNotificationString(HealthStatus status) {
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
                    });
        }
    }
}
