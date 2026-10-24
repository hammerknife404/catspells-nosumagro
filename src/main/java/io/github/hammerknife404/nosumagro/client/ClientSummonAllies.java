package io.github.hammerknife404.nosumagro.client;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.Entity;

/**
 * Client-side record of which entity ids are player-owned summons. Plain data, safe to load on either dist.
 */
public final class ClientSummonAllies {
    private static final IntSet PLAYER_SUMMONS = new IntOpenHashSet();

    private ClientSummonAllies() {
    }

    public static void set(int entityId, boolean playerOwned) {
        if (playerOwned) {
            PLAYER_SUMMONS.add(entityId);
        } else {
            PLAYER_SUMMONS.remove(entityId);
        }
    }

    public static boolean isPlayerSummon(Entity entity) {
        return PLAYER_SUMMONS.contains(entity.getId());
    }

    public static void remove(int entityId) {
        PLAYER_SUMMONS.remove(entityId);
    }

    public static void clear() {
        PLAYER_SUMMONS.clear();
    }
}
