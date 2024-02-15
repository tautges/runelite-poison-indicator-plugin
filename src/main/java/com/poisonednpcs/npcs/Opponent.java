package com.poisonednpcs.npcs;

import com.poisonednpcs.combat.HealthStatus;
import com.poisonednpcs.combat.HitTracker;
import lombok.Getter;
import net.runelite.api.NPC;

public class Opponent {

    private final NPC npc;
    @Getter
    private final int maxHealth;
    @Getter
    private final HealthStatus healthStatus;

    Opponent(NPC npc, Integer maxHealth) {
        this.npc = npc;
        this.maxHealth = maxHealth == null ? -1 : maxHealth;
        this.healthStatus = new HealthStatus(new HitTracker(() -> !npc.isDead()));
    }

    public NPC getNPC() {
        return npc;
    }

    public int getRemainingHealth() {
        int healthRatio = npc.getHealthRatio();
        int healthScale = npc.getHealthScale();

        if (healthRatio < 0 || healthScale < 0) {
            return -1;
        } else if (healthScale == 0) {
            // protecting against divide-by-zero
            return -1;
        }

        return (int)(((double)healthRatio / (double)healthScale) * maxHealth);
    }

}
