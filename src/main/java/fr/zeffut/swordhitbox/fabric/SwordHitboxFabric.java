//? if fabric {
package fr.zeffut.swordhitbox.fabric;

import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.platform.Platform;
import fr.zeffut.swordhitbox.render.HitboxRenderer;
import fr.zeffut.swordhitbox.telemetry.Telemetry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
*///?} else if >=1.21.10 {
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
*///?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric client entrypoint. Initializes config + telemetry, emits the standard
 * {@code client_started} / {@code mod_loaded} events plus a one-shot {@code swh_display_enabled}
 * event, and registers the after-entities world render hook that draws the custom interpolated
 * hitbox overlay — see {@link HitboxRenderer}.
 *
 * <p>The Fabric render-event surface diverges across versions: 1.21.11 (Yarn) exposes
 * {@code WorldRenderEvents.AFTER_ENTITIES} (package {@code ...rendering.v1.world}) with
 * {@code matrices()}/{@code consumers()} and a {@code RenderTickCounter} for the partial tick; 26.1
 * renamed it to {@code LevelRenderEvents} (package {@code ...rendering.v1.level}), so we register at
 * {@code AFTER_SOLID_FEATURES} and pull pose/buffers/camera/partial-tick from the level render
 * context and the {@code DeltaTracker}.
 */
public class SwordHitboxFabric implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("SwordHitbox");

    @Override
    public void onInitializeClient() {
        // Touch config first so install_id / telemetry opt-out are resolved before any capture.
        ModConfig cfg = ModConfig.get();

        //? if >=26.1 {
        /*LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx -> {
            Minecraft mc = Minecraft.getInstance();
            float tickProgress = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            HitboxRenderer.render(ctx.poseStack(), ctx.bufferSource(),
                    mc.gameRenderer.getMainCamera().position(), tickProgress);
        });
        *///?} else if >=1.21.10 {
        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
            HitboxRenderer.render(ctx.matrices(), ctx.consumers(),
                    mc.gameRenderer.getCamera().getCameraPos(), tickDelta);
        });
        //?} else {
        /*WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
            HitboxRenderer.render(ctx.matrixStack(), ctx.consumers(),
                    mc.gameRenderer.getCamera().getCameraPos(), tickDelta);
        });
        *///?}

        String mc = Platform.mcVersion();
        String modVer = Platform.modVersion();

        Map<String, Object> started = new LinkedHashMap<>();
        started.put("loader", "fabric");
        started.put("installed_mods_count", Platform.installedModCount());
        started.put("os_name", System.getProperty("os.name"));
        started.put("os_arch", System.getProperty("os.arch"));
        started.put("java_version", System.getProperty("java.version"));
        Telemetry.capture("client_started", "mod-fabric", mc, modVer, started);
        Telemetry.capture("mod_loaded", "mod-fabric", mc, modVer, Map.of("loader", "fabric"));

        Map<String, Object> display = new LinkedHashMap<>();
        display.put("enabled", cfg.enabled());
        display.put("render_eye_box", cfg.renderEyeBox());
        display.put("render_view_vector", cfg.renderViewVector());
        Telemetry.captureModEvent("display_enabled", "mod-fabric", mc, modVer, display);

        LOG.info("[SwordHitbox] initialized on fabric {} (telemetry={})", mc, Telemetry.enabled());
    }
}
//?}
