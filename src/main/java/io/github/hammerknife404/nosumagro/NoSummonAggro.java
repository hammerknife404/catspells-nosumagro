package io.github.hammerknife404.nosumagro;

import io.github.hammerknife404.nosumagro.client.ClientEvents;
import io.github.hammerknife404.nosumagro.network.SummonAllyPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(NoSummonAggro.MODID)
public class NoSummonAggro {
    public static final String MODID = "nosumagro";

    public NoSummonAggro(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(NoSummonAggro::registerPayloads);
        NeoForge.EVENT_BUS.register(SummonAggroHandler.class);
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.register(ClientEvents.class);
        }
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Optional: clients without this mod can still join, they just keep red health bars
        event.registrar(MODID).versioned("1").optional()
                .playToClient(SummonAllyPayload.TYPE, SummonAllyPayload.STREAM_CODEC, SummonAllyPayload::handle);
    }
}
