package io.github.hammerknife404.nosumagro.network;

import io.github.hammerknife404.nosumagro.NoSummonAggro;
import io.github.hammerknife404.nosumagro.client.ClientSummonAllies;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Tells the client whether a summon belongs to a player. ISS only knows summon owners server-side, so without
 * this the client (and health bar mods like Neat) can't tell a player's summon from a hostile one.
 */
public record SummonAllyPayload(int entityId, boolean playerOwned) implements CustomPacketPayload {
    public static final Type<SummonAllyPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(NoSummonAggro.MODID, "summon_ally"));

    public static final StreamCodec<ByteBuf, SummonAllyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SummonAllyPayload::entityId,
            ByteBufCodecs.BOOL, SummonAllyPayload::playerOwned,
            SummonAllyPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SummonAllyPayload payload, IPayloadContext context) {
        ClientSummonAllies.set(payload.entityId(), payload.playerOwned());
    }
}
