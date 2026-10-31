//? if fabric {
package fr.zeffut.swordhitbox.fabric;

import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.platform.Platform;
import fr.zeffut.swordhitbox.render.HitboxRenderer;
import fr.zeffut.swordhitbox.telemetry.Telemetry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
//? if >=26.2 {
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
//?} else if >=26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
//?} else if >=1.21.10 {
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
*///?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric client entrypoint. Initializes config + telemetry and registers the after-entities world
 * render hook that draws the interpolated hitbox overlay — see {@link HitboxRenderer}.
 */
public class SwordHitboxFabric implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("SwordHitbox");

    @Override
    public void onInitializeClient() {
        ModConfig cfg = ModConfig.get();

        //? if >=26.2 {
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx -> {
            Minecraft mc = Minecraft.getInstance();
            float tickProgress = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            Vec3 cam = mc.gameRenderer.mainCamera().position();
            HitboxRenderer.render(ctx.poseStack(), ctx.submitNodeCollector(), cam, tickProgress);
        });
        //?} else if >=26.1 {
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx -> {
            Minecraft mc = Minecraft.getInstance();
            float tickProgress = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            HitboxRenderer.render(ctx.poseStack(), ctx.bufferSource(),
                    mc.gameRenderer.getMainCamera().position(), tickProgress);
        });
        //?} else if >=1.21.10 {
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

        // A single event per launch: mod_loaded carries what client_started and
        // swh_display_enabled used to send separately.
        Map<String, Object> loaded = new LinkedHashMap<>();
        loaded.put("loader", "fabric");
        loaded.put("installed_mods_count", Platform.installedModCount());
        loaded.put("os_name", System.getProperty("os.name"));
        loaded.put("os_arch", System.getProperty("os.arch"));
        loaded.put("java_version", System.getProperty("java.version"));
        loaded.put("display_enabled", cfg.enabled());
        loaded.put("render_eye_box", cfg.renderEyeBox());
        loaded.put("render_view_vector", cfg.renderViewVector());
        Telemetry.capture("mod_loaded", "mod-fabric", mc, modVer, loaded);

        LOG.info("[SwordHitbox] initialized on fabric {} (telemetry={})", mc, Telemetry.enabled());
    }
}
//?}
