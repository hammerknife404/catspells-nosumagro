# No Summon Aggro

A NeoForge 1.21.1 mod for [Iron's Spells 'n Spellbooks](https://www.curseforge.com/minecraft/mc-mods/irons-spells-n-spellbooks). Player-owned summons from Iron's Spells, and from any addon built on its `IMagicSummon` API (Cataclysm: Spellbooks, etc.), no longer target or damage:

- other players;
- villagers, wandering traders, iron and snow golems, allays, Iron's Spells priests and Guard Villagers guards (extend the list with the tag below);
- player-owned pets and other players' summons.

Those friendly entities also stop targeting player-owned summons. Summons owned by hostile mobs (necromancers etc.) are untouched.

**Requires:** Minecraft 1.21.1, NeoForge 21.1 and Iron's Spells 'n Spellbooks 3.0 or newer. Cataclysm: Spellbooks is optional. Install it on the server, or your client for singleplayer; clients without it can still join a server that has it.

## Configuration

`config/nosumagro-common.toml`, created on first launch. Every option defaults to `true`.

| Option | Description |
|---|---|
| `protectPlayers` | Player-owned summons never target or damage other players. |
| `protectFriendlies` | Player-owned summons never target or damage villagers, traders, golems, guards, or anything in the `#nosumagro:protected_from_summons` tag. Those entities also stop targeting player-owned summons. |
| `protectPets` | Player-owned summons never target or damage tamed animals owned by a player (also while that player is offline or in another dimension), or summons owned by another player. |
| `blockDamage` | Also cancel damage dealt by player-owned summons to protected entities. This catches area and splash attacks that hit without targeting. |

## Protecting more mobs

The `#nosumagro:protected_from_summons` entity type tag lists the friendly mobs. Add more with a datapack, in `data/nosumagro/tags/entity_type/protected_from_summons.json`:

```json
{
  "replace": false,
  "values": [
    { "id": "somemod:some_friendly_mob", "required": false }
  ]
}
```

## Neat health bars

Health bars from [Neat](https://www.curseforge.com/minecraft/mc-mods/neat) show player-owned summons as friendly (green) when Neat's `color_by_type` is on. This needs the mod on the client too; clients without it just keep red bars.

## Building and releasing

`./gradlew build` writes `build/libs/nosumagro-<version>-dev.jar` (CI adds the commit; only the Release workflow builds a plain `nosumagro-<version>.jar`). To release, bump `mod_version` in `gradle.properties` and merge to `main`; the Release workflow publishes a GitHub release tagged `v<version>` with the jar. Pushes that don't change the version don't release.

This page lives in `docs/wiki/` in the repository and is copied to the wiki on every push to `main`. Edit it there: changes made on github.com are overwritten by the next sync.
