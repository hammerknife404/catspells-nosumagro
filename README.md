# No Summon Aggro (NeoForge 1.21.1)

Player-owned summons from Iron's Spells 'n Spellbooks and any addon built on its `IMagicSummon` API
(Cataclysm: Spellbooks, etc.) no longer target or damage:

- other players
- villagers, wandering traders, iron/snow golems, allays, ISS priests, Guard Villagers guards
  (extend via the `#nosumagro:protected_from_summons` entity type tag)
- player-owned pets and other players' summons

Those friendly entities also stop targeting player-owned summons. Summons owned by hostile mobs
(necromancers etc.) are untouched. Toggles live in `config/nosumagro-common.toml`.

Requires Iron's Spells 'n Spellbooks. Cataclysm: Spellbooks is optional (no compile dependency).
Build: `./gradlew build` → `build/libs/nosumagro-<version>.jar`.
