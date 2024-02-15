package com.poisonednpcs.npcs;

import net.runelite.api.NPC;

public class NPCUtils {

    /** Common function to retrieve an identifier for an NPC from the NPC. */
    public static int getIdentifier(NPC npc) {
        // the index is the identifier in the cache; we use this instead of the id because the id refers to NPC's of
        // the same model, and we could be attacking multiple NPC's with the same model.
        return npc.getIndex();
    }
}
