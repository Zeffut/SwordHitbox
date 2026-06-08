# SwordHitbox

A small **client-side** Minecraft mod that **extends your attack reach on entities while a sword is
in your main hand** — for every sword, vanilla *and* modded.

## What it does

While you hold a sword, the mod adds a transient `AttributeModifier` to your local player's
`minecraft:player.entity_interaction_range` attribute, increasing how far away you can hit mobs and
other entities. The bonus is removed the moment you switch to a non-sword item. No mixins are used.

Swords are detected via the `minecraft:swords` item tag (with a `c:swords` fallback), so the mod
catches vanilla swords, component-defined swords, and modded swords alike — it does **not** rely on
`instanceof SwordItem`.

## Multiplayer caveat (read this)

The reach attribute is changed **only on your client**. You get the full extended reach in:

- **Singleplayer**, **LAN worlds**, and
- servers that **also run this mod** (or otherwise raise the server-side interaction range).

On a **strict vanilla multiplayer server**, the server caps the effective reach at roughly **4
blocks** regardless of your client setting: it validates every attack with `canInteractWithEntity`,
which reads the **server-side** value of your interaction-range attribute. Your client may show the
swing, but hits beyond the server's allowed range are rejected. This is expected and intentional —
the mod does not (and cannot, client-side) bypass server-authoritative hit validation.

## Configuration

Settings live in `config/swordhitbox.json` (created on first run) under the `settings` object:

| Key           | Type    | Default | Notes                                              |
|---------------|---------|---------|----------------------------------------------------|
| `enabled`     | boolean | `true`  | Master switch. When `false`, no bonus is applied.  |
| `reach_bonus` | double  | `2.0`   | Extra blocks of reach. Clamped to `0..16`.         |

The base interaction range is `3.0`, so the default `reach_bonus` of `2.0` yields `5.0` blocks of
client-side reach while a sword is held.

## Telemetry

The mod emits **anonymous** usage telemetry to PostHog (a stable random `install_id`, MC/loader
versions, and a one-shot `swh_enabled` event carrying your `enabled` / `reach_bonus` values). No
personal data, no IP-based geolocation, no per-tick events.

**Opt out** any of these ways:

- set `"telemetry": false` in `config/swordhitbox.json`;
- pass `-Dswordhitbox.telemetry=false` to the JVM;
- dev environments never emit telemetry.

## Loaders & versions

- **Fabric** and **NeoForge**
- Minecraft **1.21.11** and **26.1.2**

Requires Fabric API on Fabric. Client-only on both loaders.

## License

[PolyForm Noncommercial License 1.0.0](./LICENSE).
