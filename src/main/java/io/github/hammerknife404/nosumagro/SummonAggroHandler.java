package io.github.hammerknife404.nosumagro;

import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.github.hammerknife404.nosumagro.network.SummonAllyPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Every Iron's Spells summon (and every addon summon built on {@link IMagicSummon}, e.g. Cataclysm: Spellbooks)
 * picks targets through {@link Mob#setTarget}, which NeoForge routes through {@link LivingChangeTargetEvent}.
 * Vetoing there covers ISS's owner goals as well as the vanilla/Cataclysm NearestAttackableTargetGoals some
 * addon summons still carry.
 */
public final class SummonAggroHandler {
    public static final TagKey<EntityType<?>> PROTECTED_FROM_SUMMONS =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(NoSummonAggro.MODID, "protected_from_summons"));

    private static final int CLEANUP_INTERVAL = 10;

    /** Last player-owned state sent to clients per summon, so we only broadcast on change. */
    private static final Map<Entity, Boolean> SYNCED_OWNERSHIP = new WeakHashMap<>();

    private SummonAggroHandler() {
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();
        if (newTarget == null || entity.level().isClientSide) {
            return;
        }

        Player owner = playerOwner(entity);
        if (owner != null && isProtectedFrom(owner, newTarget)) {
            event.setCanceled(true);
            return;
        }

        // Golems/guards shouldn't start fights with player summons they can't be hit back by
        if (Config.PROTECT_FRIENDLIES.get() && isFriendly(entity) && playerOwner(newTarget) != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!Config.BLOCK_DAMAGE.get()) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        Player owner = playerOwner(event.getSource().getEntity());
        if (owner != null && isProtectedFrom(owner, victim)) {
            event.setCanceled(true);
        }
    }

    /**
     * Drops a protected target that slipped in before this mod could veto it (pre-existing save data, or a
     * brain memory written directly instead of through setTarget), and syncs player ownership to clients.
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob) || !(mob instanceof IMagicSummon)
                || mob.level().isClientSide || mob.tickCount % CLEANUP_INTERVAL != 0) {
            return;
        }
        Player owner = playerOwner(mob);
        syncOwnership(mob, owner != null);
        if (owner == null) {
            return;
        }

        LivingEntity target = mob.getTarget();
        if (target != null && isProtectedFrom(owner, target)) {
            mob.setTarget(null);
        }

        Brain<?> brain = mob.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            brain.getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(memoryTarget -> {
                if (isProtectedFrom(owner, memoryTarget)) {
                    brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof IMagicSummon && event.getEntity() instanceof ServerPlayer player
                && playerOwner(event.getTarget()) != null) {
            send(player, new SummonAllyPayload(event.getTarget().getId(), true));
        }
    }

    private static void syncOwnership(Mob mob, boolean playerOwned) {
        if (SYNCED_OWNERSHIP.getOrDefault(mob, false) == playerOwned) {
            return;
        }
        SYNCED_OWNERSHIP.put(mob, playerOwned);
        var payload = new SummonAllyPayload(mob.getId(), playerOwned);
        // Whole dimension rather than trackers: StartTracking may have fired before the owner was assigned
        for (ServerPlayer player : ((ServerLevel) mob.level()).players()) {
            send(player, payload);
        }
    }

    private static void send(ServerPlayer player, SummonAllyPayload payload) {
        // Payload is optional; sending to a client without the channel throws
        if (player.connection.hasChannel(SummonAllyPayload.TYPE)) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    /**
     * @return the player who summoned this entity, or null if it isn't a player's summon (hostile casters such
     * as ISS necromancers keep their normal behaviour)
     */
    @Nullable
    private static Player playerOwner(@Nullable Entity entity) {
        if (entity instanceof IMagicSummon summon && summon.getSummoner() instanceof Player player) {
            return player;
        }
        return null;
    }

    private static boolean isFriendly(LivingEntity entity) {
        return entity instanceof AbstractVillager || entity.getType().is(PROTECTED_FROM_SUMMONS);
    }

    private static boolean isProtectedFrom(Player owner, LivingEntity target) {
        if (target == owner) {
            return true;
        }
        if (target instanceof Player) {
            return Config.PROTECT_PLAYERS.get();
        }
        if (Config.PROTECT_FRIENDLIES.get() && isFriendly(target)) {
            return true;
        }
        if (Config.PROTECT_PETS.get()) {
            if (target instanceof OwnableEntity ownable && ownable.getOwner() instanceof Player) {
                return true;
            }
            return playerOwner(target) != null;
        }
        return false;
    }
}
