package io.github.hammerknife404.nosumagro.mixin;

import io.github.hammerknife404.nosumagro.client.ClientSummonAllies;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Neat colors bars by entity category (monster = red). Player-owned summons get the friendly green instead.
 * Pseudo + require 0: silently skipped when Neat isn't installed or its method changes.
 */
@Pseudo
@Mixin(targets = "vazkii.neat.HealthBarRenderer", remap = false)
public abstract class NeatHealthBarRendererMixin {
    private static final int FRIENDLY_GREEN = 0xff00ff00;

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true, require = 0)
    private static void nosumagro$allyColor(LivingEntity entity, boolean colorByType, boolean boss, CallbackInfoReturnable<Integer> cir) {
        if (colorByType && !boss && ClientSummonAllies.isPlayerSummon(entity)) {
            cir.setReturnValue(FRIENDLY_GREEN);
        }
    }
}
