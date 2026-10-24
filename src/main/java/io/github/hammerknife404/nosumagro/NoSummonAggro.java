package io.github.hammerknife404.nosumagro;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(NoSummonAggro.MODID)
public class NoSummonAggro {
    public static final String MODID = "nosumagro";

    public NoSummonAggro(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(SummonAggroHandler.class);
    }
}
