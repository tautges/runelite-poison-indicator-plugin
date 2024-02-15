package com.poisonednpcs.npcs;

import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.client.game.NPCManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Tracks which NPCs are currently considered to be in combat with the client's player.
 */
@Singleton
public class NPCTrackingService {

    private final Map<Integer, Opponent> inCombat = new HashMap<>();

    @Getter
    private NPCManager manager;

    @Inject
    private NPCTrackingService(NPCManager manager) {
        this.manager = manager;
    }

    public void enterCombat(NPC npc) {
        if (inCombat.containsKey(NPCUtils.getIdentifier(npc))) {
            // nothing to do
            return;
        }
        inCombat.put(NPCUtils.getIdentifier(npc), new Opponent(npc, manager.getHealth(npc.getId())));
    }

    public Opponent getOpponent(int npcId) {
        Opponent opponent = inCombat.get(npcId);
        if (opponent == null) {
            // TODO: make this a specific exception type?
            throw new RuntimeException(String.format("not in combat with opponent: %d", npcId));
        }
        return opponent;
    }

    public void cullIf(Predicate<Opponent> condition) {
        // copy out the list of ids and force to materialize to avoid concurrency issues when we modify the map
        List<Integer> ids = new ArrayList<>(inCombat.keySet());
        for (int id : ids) {
            if (condition.test(inCombat.get(id))) {
                inCombat.remove(id);
            }
        }
    }

    public void forEachCombat(Consumer<Opponent> forEach) {
        inCombat.forEach((id, opponent) -> forEach.accept(opponent));
    }
}
