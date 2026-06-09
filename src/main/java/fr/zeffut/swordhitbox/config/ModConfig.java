package fr.zeffut.swordhitbox.config;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Typed JSON config manager for {@code config/swordhitbox.json}. Mapping-agnostic: references no
 * Minecraft class and pulls in no JSON library (hand-rolled minimal reader/writer), so it compiles
 * identically across every Stonecutter node (Yarn / Mojmap, Fabric / NeoForge) and adds no runtime
 * dependency.
 *
 * <p>Drives the custom hitbox overlay ({@code render/HitboxRenderer}) and the Cloth Config screen
 * ({@code config/ConfigScreenFactory}). Every setter persists immediately via {@link #save()}.
 *
 * <p>Schema (defaults shown):
 * <pre>
 * {
 *   "telemetry": true,                 // master telemetry opt-out
 *   "install_id": "&lt;uuid&gt;",      // stable anonymous id, generated once
 *   "enabled": true,                   // draw the overlay while holding a sword
 *   "renderEyeBox": true,              // thin band at eye height
 *   "renderViewVector": true,          // look-direction line
 *   "changeTargetColor": false,        // recolor the box of the crosshair-targeted entity
 *   "boxColor": -1,                    // ARGB white  (0xFFFFFFFF)
 *   "eyeColor": -65536,                // ARGB red    (0xFFFF0000)
 *   "viewColor": -16776961,            // ARGB blue   (0xFF0000FF)
 *   "targetColor": -65536,             // ARGB red    (0xFFFF0000)
 *   "lineWidth": 2.5,                  // line width, 0..25
 *   "radius": 24.0                     // entity scan radius, 4..64
 * }
 * </pre>
 */
public final class ModConfig {

    private static final String FILE_NAME = "swordhitbox.json";
    private static volatile ModConfig instance;

    // ---- defaults --------------------------------------------------------------------------
    public static final boolean DEF_ENABLED = true;
    public static final boolean DEF_PLAYERS_ONLY = true;
    public static final boolean DEF_RENDER_EYE_BOX = true;
    public static final boolean DEF_RENDER_VIEW_VECTOR = true;
    public static final boolean DEF_CHANGE_TARGET_COLOR = false;
    public static final int DEF_BOX_COLOR = 0xFFFFFFFF;     // white
    public static final int DEF_EYE_COLOR = 0xFFFF0000;     // red
    public static final int DEF_VIEW_COLOR = 0xFF0000FF;    // blue
    public static final int DEF_TARGET_COLOR = 0xFFFF0000;  // red
    public static final float DEF_LINE_WIDTH = 2.5f;
    public static final float LINE_WIDTH_MIN = 0f;
    public static final float LINE_WIDTH_MAX = 25f;
    public static final double DEF_RADIUS = 24.0;
    public static final double RADIUS_MIN = 4.0;
    public static final double RADIUS_MAX = 64.0;

    // ---- fields ----------------------------------------------------------------------------
    private boolean telemetry = true;
    private String installId;

    private boolean enabled = DEF_ENABLED;
    private boolean playersOnly = DEF_PLAYERS_ONLY;
    private boolean renderEyeBox = DEF_RENDER_EYE_BOX;
    private boolean renderViewVector = DEF_RENDER_VIEW_VECTOR;
    private boolean changeTargetColor = DEF_CHANGE_TARGET_COLOR;
    private int boxColor = DEF_BOX_COLOR;
    private int eyeColor = DEF_EYE_COLOR;
    private int viewColor = DEF_VIEW_COLOR;
    private int targetColor = DEF_TARGET_COLOR;
    private float lineWidth = DEF_LINE_WIDTH;
    private double radius = DEF_RADIUS;

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

    // ---- accessors -------------------------------------------------------------------------
    public boolean telemetry() { return telemetry; }
    public void setTelemetry(boolean v) { this.telemetry = v; save(); }

    public String installId() { return installId; }

    public boolean enabled() { return enabled; }
    public void setEnabled(boolean v) { this.enabled = v; save(); }

    public boolean playersOnly() { return playersOnly; }
    public void setPlayersOnly(boolean v) { this.playersOnly = v; save(); }

    public boolean renderEyeBox() { return renderEyeBox; }
    public void setRenderEyeBox(boolean v) { this.renderEyeBox = v; save(); }

    public boolean renderViewVector() { return renderViewVector; }
    public void setRenderViewVector(boolean v) { this.renderViewVector = v; save(); }

    public boolean changeTargetColor() { return changeTargetColor; }
    public void setChangeTargetColor(boolean v) { this.changeTargetColor = v; save(); }

    public int boxColor() { return boxColor; }
    public void setBoxColor(int v) { this.boxColor = v; save(); }

    public int eyeColor() { return eyeColor; }
    public void setEyeColor(int v) { this.eyeColor = v; save(); }

    public int viewColor() { return viewColor; }
    public void setViewColor(int v) { this.viewColor = v; save(); }

    public int targetColor() { return targetColor; }
    public void setTargetColor(int v) { this.targetColor = v; save(); }

    public float lineWidth() { return lineWidth; }
    public void setLineWidth(float v) { this.lineWidth = clampF(v, LINE_WIDTH_MIN, LINE_WIDTH_MAX); save(); }

    public double radius() { return radius; }
    public void setRadius(double v) { this.radius = clampD(v, RADIUS_MIN, RADIUS_MAX); save(); }

    // ---- persistence -----------------------------------------------------------------------
    private static File file() { return new File("config", FILE_NAME); }

    private static ModConfig load() {
        ModConfig cfg = new ModConfig();
        boolean existed = false;
        try {
            File f = file();
            if (f.exists()) {
                existed = true;
                String c = Files.readString(f.toPath());
                cfg.telemetry = parseBool(c, "telemetry", true);
                cfg.installId = extractString(c, "install_id");
                cfg.enabled = parseBool(c, "enabled", DEF_ENABLED);
                cfg.playersOnly = parseBool(c, "playersOnly", DEF_PLAYERS_ONLY);
                cfg.renderEyeBox = parseBool(c, "renderEyeBox", DEF_RENDER_EYE_BOX);
                cfg.renderViewVector = parseBool(c, "renderViewVector", DEF_RENDER_VIEW_VECTOR);
                cfg.changeTargetColor = parseBool(c, "changeTargetColor", DEF_CHANGE_TARGET_COLOR);
                cfg.boxColor = parseInt(c, "boxColor", DEF_BOX_COLOR);
                cfg.eyeColor = parseInt(c, "eyeColor", DEF_EYE_COLOR);
                cfg.viewColor = parseInt(c, "viewColor", DEF_VIEW_COLOR);
                cfg.targetColor = parseInt(c, "targetColor", DEF_TARGET_COLOR);
                cfg.lineWidth = clampF((float) parseNumber(c, "lineWidth", DEF_LINE_WIDTH),
                        LINE_WIDTH_MIN, LINE_WIDTH_MAX);
                cfg.radius = clampD(parseNumber(c, "radius", DEF_RADIUS), RADIUS_MIN, RADIUS_MAX);
            }
        } catch (Throwable ignored) {
            // fall through to defaults
        }
        boolean needSave = !existed;
        if (cfg.installId == null || cfg.installId.isBlank()) {
            cfg.installId = UUID.randomUUID().toString();
            needSave = true;
        }
        if (needSave) {
            cfg.save();
        }
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
            sb.append("  \"enabled\": ").append(enabled).append(",\n");
            sb.append("  \"playersOnly\": ").append(playersOnly).append(",\n");
            sb.append("  \"renderEyeBox\": ").append(renderEyeBox).append(",\n");
            sb.append("  \"renderViewVector\": ").append(renderViewVector).append(",\n");
            sb.append("  \"changeTargetColor\": ").append(changeTargetColor).append(",\n");
            sb.append("  \"boxColor\": ").append(boxColor).append(",\n");
            sb.append("  \"eyeColor\": ").append(eyeColor).append(",\n");
            sb.append("  \"viewColor\": ").append(viewColor).append(",\n");
            sb.append("  \"targetColor\": ").append(targetColor).append(",\n");
            sb.append("  \"lineWidth\": ").append(lineWidth).append(",\n");
            sb.append("  \"radius\": ").append(radius).append("\n");
            sb.append("}\n");
            Files.writeString(f.toPath(), sb.toString());
        } catch (Throwable ignored) {
            // best-effort
        }
    }

    // ---- minimal JSON readers --------------------------------------------------------------
    private static String rawValue(String json, String key) {
        int i = json.indexOf("\"" + key + "\"");
        if (i < 0) return null;
        int colon = json.indexOf(':', i + key.length() + 2);
        if (colon < 0) return null;
        int p = colon + 1;
        int n = json.length();
        while (p < n && Character.isWhitespace(json.charAt(p))) p++;
        int start = p;
        while (p < n) {
            char ch = json.charAt(p);
            if (ch == ',' || ch == '}' || ch == '\n' || ch == '\r') break;
            p++;
        }
        return json.substring(start, p).trim();
    }

    private static boolean parseBool(String json, String key, boolean def) {
        String v = rawValue(json, key);
        if (v == null) return def;
        if (v.equalsIgnoreCase("true")) return true;
        if (v.equalsIgnoreCase("false")) return false;
        return def;
    }

    private static int parseInt(String json, String key, int def) {
        String v = rawValue(json, key);
        if (v == null) return def;
        try { return (int) Long.parseLong(v); } catch (NumberFormatException e) { return def; }
    }

    private static double parseNumber(String json, String key, double def) {
        String v = rawValue(json, key);
        if (v == null) return def;
        try { return Double.parseDouble(v); } catch (NumberFormatException e) { return def; }
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

    private static float clampF(float v, float lo, float hi) { return v < lo ? lo : (v > hi ? hi : v); }
    private static double clampD(double v, double lo, double hi) { return v < lo ? lo : (v > hi ? hi : v); }
}
