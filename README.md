# SwordHitbox

A small **client-side, purely visual** Minecraft mod (in the spirit of *Mace Hitboxes*): while you
hold a **sword**, it draws the collision-box outline of nearby living entities so you can read PvP
and mob fights at a glance.

## What it does

When a sword is in your main hand, the mod outlines the hitbox of every living entity within a
configurable radius around you. Optionally, entities that are within your attack range can be tinted
a different color.

Swords are detected via the `minecraft:swords` item tag (with a `c:swords` fallback), so it covers
vanilla swords, component-defined swords, and modded swords alike — it does **not** rely on
`instanceof SwordItem`.

**This is not a cheat.** Nothing is sent to the server: the outlines are drawn locally on your
client only. The mod changes **no gameplay** — no attribute is modified, no reach is extended, no
packets are altered. It therefore works on **every server**, including strict vanilla anti-cheat
servers, exactly like any rendering-only client mod.

## Configuration

Settings live in `config/swordhitbox.json` (created on first run) under the `settings` object:

| Key                  | Type    | Default      | Notes                                                              |
|----------------------|---------|--------------|--------------------------------------------------------------------|
| `enabled`            | boolean | `true`       | Master switch. When `false`, nothing is drawn.                     |
| `highlight_in_range` | boolean | `false`      | When `true`, in-range entities use `in_range_color`. Off by default. |
| `box_color`          | ARGB    | `0xFFFFFFFF` | Outline color for every nearby entity (white).                     |
| `in_range_color`     | ARGB    | `0xFFFF0000` | Outline color for in-range entities (red), only if highlighting is on. |
| `radius`             | double  | `24.0`       | Search radius (blocks) for living entities. Clamped to `4..64`.    |

Colors are packed ARGB and may be written as `0xAARRGGBB`, `#AARRGGBB`, or a decimal literal.

The in-range test uses the vanilla `isWithinEntityInteractionRange` helper (no custom distance math),
so the red tint matches what the game itself considers reachable.

## Telemetry

The mod emits **anonymous** usage telemetry to PostHog (a stable random `install_id`, MC/loader
versions, and a one-shot `swh_display_enabled` event carrying your `enabled` / `highlight_in_range`
values). No personal data, no IP-based geolocation, no per-frame events.

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
