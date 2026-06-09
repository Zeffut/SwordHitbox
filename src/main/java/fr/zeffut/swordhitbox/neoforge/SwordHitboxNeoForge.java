//? if neoforge {
package fr.zeffut.swordhitbox.neoforge;

import fr.zeffut.swordhitbox.config.ConfigScreenFactory;
import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.platform.Platform;
import fr.zeffut.swordhitbox.render.HitboxRenderer;
import fr.zeffut.swordhitbox.telemetry.Telemetry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NeoForge client entrypoint. Initializes config + telemetry, emits the standard
 * {@code client_started} / {@code mod_loaded} events plus a one-shot {@code swh_display_enabled}
 * event, subscribes the after-entities render-level hook that draws the custom interpolated hitbox
 * overlay ({@link HitboxRenderer}), and registers the in-game "Config" button that opens the shared
 * Cloth Config screen ({@link ConfigScreenFactory}).
 */
@Mod(value = "swordhitbox", dist = Dist.CLIENT)
public class SwordHitboxNeoForge {
    private static final Logger LOG = LoggerFactory.getLogger("SwordHitbox");

    public SwordHitboxNeoForge(IEventBus modBus, ModContainer container) {
        // Touch config first so install_id / telemetry opt-out are resolved before any capture.
        ModConfig cfg = ModConfig.get();

        // The "after entities" stage differs by version: 1.21.11 exposes a dedicated
        // RenderLevelStageEvent.AfterEntities subtype; 26.1 dropped it, so we draw the world-space
        // line overlay at the AfterTranslucentBlocks stage (after entities & blocks are rendered).
        //? if >=26.1 {
        /*NeoForge.EVENT_BUS.addListener(
                (RenderLevelStageEvent.AfterTranslucentBlocks event) -> onRenderAfterEntities(event));
        *///?} else {
        NeoForge.EVENT_BUS.addListener(
                (RenderLevelStageEvent.AfterEntities event) -> onRenderAfterEntities(event));
        //?}

        // Native "Config" button in the NeoForge mod list, opening the shared Cloth Config screen.
        // IConfigScreenFactory.createScreen(ModContainer, Screen) has the same signature on 1.21.11
        // and 26.1.2. The mod is @Mod(dist = CLIENT), so this constructor only runs client-side.
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (IConfigScreenFactory) (c, parent) -> ConfigScreenFactory.create(parent));

        String mc = Platform.mcVersion();
        String modVer = Platform.modVersion();

        Map<String, Object> started = new LinkedHashMap<>();
        started.put("loader", "neoforge");
        started.put("installed_mods_count", Platform.installedModCount());
        started.put("os_name", System.getProperty("os.name"));
        started.put("os_arch", System.getProperty("os.arch"));
        started.put("java_version", System.getProperty("java.version"));
        Telemetry.capture("client_started", "mod-neoforge", mc, modVer, started);
        Telemetry.capture("mod_loaded", "mod-neoforge", mc, modVer, Map.of("loader", "neoforge"));

        Map<String, Object> display = new LinkedHashMap<>();
        display.put("enabled", cfg.enabled());
        display.put("render_eye_box", cfg.renderEyeBox());
        display.put("render_view_vector", cfg.renderViewVector());
        Telemetry.captureModEvent("display_enabled", "mod-neoforge", mc, modVer, display);

        LOG.info("[SwordHitbox] initialized on neoforge {} (telemetry={})", mc, Telemetry.enabled());
    }

    private static void onRenderAfterEntities(RenderLevelStageEvent event) {
        PoseStack pose = event.getPoseStack();
        if (pose == null) return;
        Minecraft mc = Minecraft.getInstance();
        Vec3 camPos = mc.gameRenderer.getMainCamera().position();
        float tickProgress = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        HitboxRenderer.render(pose, buffers, camPos, tickProgress);
        buffers.endBatch(RenderTypes.lines());
    }
}
//?}
