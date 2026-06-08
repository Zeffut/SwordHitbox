# SwordHitbox

A small **client-side, purely visual** Minecraft mod: while you hold a **sword**, it shows the
**vanilla entity hitboxes** — exactly the ones from the F3+B debug overlay — so you can read PvP and
mob fights at a glance.

## What it does

When a sword is in your main hand, the mod turns on Minecraft's own entity-hitbox debug rendering
for every entity: the interpolated white collision box, the red eye box, and the blue look vector —
identical to pressing **F3+B**. When you stop holding a sword, the overlay is turned back off. No
custom geometry is drawn; the mod only flips the game's native render-hitboxes flag.

It respects a **manually toggled F3+B**: the mod snapshots your current state when it forces the
overlay on, and restores that exact state when you put the sword away. So if you had F3+B on
yourself, it stays on.

Swords are detected via the `minecraft:swords` item tag (with a `c:swords` fallback), so it covers
vanilla swords, component-defined swords, and modded swords alike — it does **not** rely on
`instanceof SwordItem`.

**This is not a cheat.** Nothing is sent to the server: the overlay is rendered locally on your
client only. The mod changes **no gameplay** — no attribute is modified, no reach is extended, no
packets are altered. It therefore works on **every server**, including strict vanilla anti-cheat
servers, exactly like any rendering-only client mod.

## Configuration

Settings live in `config/swordhitbox.json` (created on first run) under the `settings` object:

| Key       | Type    | Default | Notes                                                       |
|-----------|---------|---------|-------------------------------------------------------------|
| `enabled` | boolean | `true`  | Master switch. When `false`, the overlay is never toggled on. |

> Planned (not yet implemented): an optional red highlight of entities within attack range, via a
> mixin. The current build shows the plain vanilla hitboxes only.

## Telemetry

The mod emits **anonymous** usage telemetry to PostHog (a stable random `install_id`, MC/loader
versions, and a one-shot `swh_hitboxes_shown` event carrying your `enabled` value). No personal
data, no IP-based geolocation, no per-frame events.

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
