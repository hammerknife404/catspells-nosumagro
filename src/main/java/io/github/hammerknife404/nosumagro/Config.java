package io.github.hammerknife404.nosumagro;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue PROTECT_PLAYERS = BUILDER
            .comment("Player-owned summons never target or damage other players.")
            .define("protectPlayers", true);

    public static final ModConfigSpec.BooleanValue PROTECT_FRIENDLIES = BUILDER
            .comment("Player-owned summons never target or damage villagers, traders, golems, guards,",
                    "or anything in the #nosumagro:protected_from_summons entity type tag.",
                    "Those entities also stop targeting player-owned summons.")
            .define("protectFriendlies", true);

    public static final ModConfigSpec.BooleanValue PROTECT_PETS = BUILDER
            .comment("Player-owned summons never target or damage tamed animals owned by a player,",
                    "or summons owned by another player.")
            .define("protectPets", true);

    public static final ModConfigSpec.BooleanValue BLOCK_DAMAGE = BUILDER
            .comment("Also cancel damage dealt by player-owned summons to protected entities",
                    "(catches AoE/splash attacks that hit without targeting).")
            .define("blockDamage", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
