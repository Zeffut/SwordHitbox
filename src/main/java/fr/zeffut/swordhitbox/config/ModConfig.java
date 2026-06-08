package fr.zeffut.swordhitbox.config;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JSON config manager for {@code config/swordhitbox.json}. Mapping-agnostic: references no Minecraft
 * class, uses no JSON library (hand-rolled minimal reader/writer) so it compiles identically across
 * every node and pulls in no extra dependency.
 *
 * <p>Schema:
 * <pre>
 * {
 *   "telemetry": true,             // bool, default true — master opt-out switch
 *   "install_id": "&lt;uuid&gt;",  // stable anonymous id, generated once
 *   "settings": { ... }            // free-form map of mod-specific string settings
 * }
 * </pre>
 *
 * <p>The file is created on first access. A single instance is cached via {@link #get()}.
 */
public final class ModConfig {

    private static final String FILE_NAME = "swordhitbox.json";
    private static volatile ModConfig instance;

    /** Whether the hitbox overlay is drawn at all. */
    private static final String KEY_ENABLED = "enabled";
    private static final boolean DEFAULT_ENABLED = true;
    /** Whether entities within attack range are tinted with {@code in_range_color}. */
    private static final String KEY_HIGHLIGHT_IN_RANGE = "highlight_in_range";
    private static final boolean DEFAULT_HIGHLIGHT_IN_RANGE = false;
    /** Packed ARGB color for the hitbox outline of every nearby living entity. */
    private static final String KEY_BOX_COLOR = "box_color";
    private static final int DEFAULT_BOX_COLOR = 0xFFFFFFFF;
    /** Packed ARGB color used only for in-range entities when highlighting is on. */
    private static final String KEY_IN_RANGE_COLOR = "in_range_color";
    private static final int DEFAULT_IN_RANGE_COLOR = 0xFFFF0000;
    /** Search radius (blocks) around the player for living entities to outline. */
    private static final String KEY_RADIUS = "radius";
    private static final double DEFAULT_RADIUS = 24.0;
    private static final double MIN_RADIUS = 4.0;
    private static final double MAX_RADIUS = 64.0;

    private boolean telemetry = true;
    private String installId;
    private final Map<String, String> settings = new LinkedHashMap<>();

    private ModConfig() {}

    /** Lazily loads (and creates if missing) the config from {@code config/swordhitbox.json}. */
    public static ModConfig get() {
        ModConfig local = instance;
        if (local == null) {
            synchronized (ModConfig.class) {
                local = instance;
                if (local == null) {
                    local = load();
                    instance = local;
                }
            }
        }
        return local;
    }

    public boolean telemetry() { return telemetry; }

    public void setTelemetry(boolean value) { this.telemetry = value; save(); }

    public String installId() { return installId; }

    /** Mod-specific extensible settings. Call {@link #save()} after mutating. */
    public Map<String, String> settings() { return settings; }

    public String setting(String key, String fallback) {
        return settings.getOrDefault(key, fallback);
    }

    /** Whether the hitbox overlay is drawn at all. Default {@code true}. */
    public boolean enabled() {
        return Boolean.parseBoolean(settings.getOrDefault(KEY_ENABLED, String.valueOf(DEFAULT_ENABLED)));
    }

    /**
     * Whether entities within attack range are drawn in {@link #inRangeColor()} instead of
     * {@link #boxColor()}. Default {@code false} (the red "in range" tint is opt-in).
     */
    public boolean highlightInRange() {
        return Boolean.parseBoolean(
                settings.getOrDefault(KEY_HIGHLIGHT_IN_RANGE, String.valueOf(DEFAULT_HIGHLIGHT_IN_RANGE)));
    }

    /** Packed ARGB color for every nearby entity's hitbox. Default white {@code 0xFFFFFFFF}. */
    public int boxColor() {
        return parseColor(settings.get(KEY_BOX_COLOR), DEFAULT_BOX_COLOR);
    }

    /** Packed ARGB color for in-range entities (only used if {@link #highlightInRange()}). */
    public int inRangeColor() {
        return parseColor(settings.get(KEY_IN_RANGE_COLOR), DEFAULT_IN_RANGE_COLOR);
    }

    /** Search radius (blocks) for nearby living entities. Clamped to {@code 4..64}. */
    public double radius() {
        double v = DEFAULT_RADIUS;
        String raw = settings.get(KEY_RADIUS);
        if (raw != null) {
            try {
                v = Double.parseDouble(raw.trim());
            } catch (NumberFormatException ignored) {
                v = DEFAULT_RADIUS;
            }
        }
        return Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, v));
    }

    /**
     * Parses an ARGB color stored as a hex string ({@code "0xFFFFFFFF"}, {@code "#FFFFFFFF"} or a
     * bare hex/decimal literal). Falls back to {@code fallback} on any parse failure.
     */
    private static int parseColor(String raw, int fallback) {
        if (raw == null) return fallback;
        String s = raw.trim();
        if (s.isEmpty()) return fallback;
        try {
            if (s.startsWith("0x") || s.startsWith("0X")) {
                return (int) Long.parseLong(s.substring(2), 16);
            }
            if (s.startsWith("#")) {
                return (int) Long.parseLong(s.substring(1), 16);
            }
            // Plain decimal, or hex without prefix is not assumed; try decimal first.
            return (int) Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            try {
                return (int) Long.parseLong(s, 16);
            } catch (NumberFormatException ignored2) {
                return fallback;
            }
        }
    }

    /** Writes the mod's setting defaults if absent, persisting once on first run. */
    private void ensureDefaults() {
        boolean changed = false;
        if (!settings.containsKey(KEY_ENABLED)) {
            settings.put(KEY_ENABLED, String.valueOf(DEFAULT_ENABLED));
            changed = true;
        }
        if (!settings.containsKey(KEY_HIGHLIGHT_IN_RANGE)) {
            settings.put(KEY_HIGHLIGHT_IN_RANGE, String.valueOf(DEFAULT_HIGHLIGHT_IN_RANGE));
            changed = true;
        }
        if (!settings.containsKey(KEY_BOX_COLOR)) {
            settings.put(KEY_BOX_COLOR, toHex(DEFAULT_BOX_COLOR));
            changed = true;
        }
        if (!settings.containsKey(KEY_IN_RANGE_COLOR)) {
            settings.put(KEY_IN_RANGE_COLOR, toHex(DEFAULT_IN_RANGE_COLOR));
            changed = true;
        }
        if (!settings.containsKey(KEY_RADIUS)) {
            settings.put(KEY_RADIUS, String.valueOf(DEFAULT_RADIUS));
            changed = true;
        }
        if (changed) save();
    }

    private static String toHex(int argb) {
        return String.format("0x%08X", argb);
    }

    public void putSetting(String key, String value) { settings.put(key, value); save(); }

    private static File file() { return new File("config", FILE_NAME); }

    private static ModConfig load() {
        ModConfig cfg = new ModConfig();
        try {
            File f = file();
            if (f.exists()) {
                String c = Files.readString(f.toPath());
                cfg.telemetry = !c.replaceAll("\\s", "").contains("\"telemetry\":false");
                cfg.installId = extractString(c, "install_id");
                parseSettings(c, cfg.settings);
            }
        } catch (Throwable ignored) {
            // fall through to defaults
        }
        if (cfg.installId == null || cfg.installId.isBlank()) {
            cfg.installId = UUID.randomUUID().toString();
            cfg.save();
        }
        cfg.ensureDefaults();
        return cfg;
    }

    /** Persists the current state. Best-effort; failures are swallowed. */
    public void save() {
        try {
            File f = file();
            File dir = f.getParentFile();
            if (dir != null) dir.mkdirs();
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"telemetry\": ").append(telemetry).append(",\n");
            sb.append("  \"install_id\": \"").append(esc(installId)).append("\",\n");
            sb.append("  \"settings\": {");
            boolean first = true;
            for (Map.Entry<String, String> e : settings.entrySet()) {
                sb.append(first ? "\n" : ",\n");
                first = false;
                sb.append("    \"").append(esc(e.getKey())).append("\": \"")
                        .append(esc(e.getValue())).append('"');
            }
            sb.append(settings.isEmpty() ? "}" : "\n  }").append("\n}\n");
            Files.writeString(f.toPath(), sb.toString());
        } catch (Throwable ignored) {
            // best-effort
        }
    }

    /**
     * Minimal parser for the flat {@code "settings": { "k": "v", ... }} object. Values are stored
     * as strings (the only type this config emits); whitespace is tolerated. Best-effort: a
     * malformed block simply yields no entries and the defaults take over.
     */
    private static void parseSettings(String json, Map<String, String> out) {
        int i = json.indexOf("\"settings\"");
        if (i < 0) return;
        int open = json.indexOf('{', i);
        if (open < 0) return;
        int close = json.indexOf('}', open);
        if (close < 0) return;
        String body = json.substring(open + 1, close);
        int p = 0;
        while (p < body.length()) {
            int q1 = body.indexOf('"', p);
            if (q1 < 0) break;
            int q2 = body.indexOf('"', q1 + 1);
            if (q2 < 0) break;
            String key = body.substring(q1 + 1, q2);
            int colon = body.indexOf(':', q2 + 1);
            if (colon < 0) break;
            int v1 = body.indexOf('"', colon + 1);
            if (v1 < 0) break;
            int v2 = body.indexOf('"', v1 + 1);
            if (v2 < 0) break;
            out.put(key, body.substring(v1 + 1, v2));
            p = v2 + 1;
        }
    }

    private static String extractString(String json, String key) {
        int i = json.indexOf("\"" + key + "\"");
        if (i < 0) return null;
        int colon = json.indexOf(':', i);
        if (colon < 0) return null;
        int q1 = json.indexOf('"', colon + 1);
        int q2 = q1 < 0 ? -1 : json.indexOf('"', q1 + 1);
        if (q1 < 0 || q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
