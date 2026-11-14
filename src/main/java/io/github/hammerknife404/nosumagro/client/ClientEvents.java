package io.github.hammerknife404.nosumagro.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            ClientSummonAllies.remove(event.getEntity().getId());
        }
    }

    /** Dimension change drops the old level's entities without leave events; the server re-sends on tracking. */
    @SubscribeEvent
    public static void onClone(ClientPlayerNetworkEvent.Clone event) {
        ClientSummonAllies.clear();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSummonAllies.clear();
    }
}
