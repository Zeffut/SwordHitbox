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
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
*///?}
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

        // A single event per launch: mod_loaded carries what client_started and
        // swh_display_enabled used to send separately.
        Map<String, Object> loaded = new LinkedHashMap<>();
        loaded.put("loader", "neoforge");
        loaded.put("installed_mods_count", Platform.installedModCount());
        loaded.put("os_name", System.getProperty("os.name"));
        loaded.put("os_arch", System.getProperty("os.arch"));
        loaded.put("java_version", System.getProperty("java.version"));
        loaded.put("display_enabled", cfg.enabled());
        loaded.put("render_eye_box", cfg.renderEyeBox());
        loaded.put("render_view_vector", cfg.renderViewVector());
        Telemetry.capture("mod_loaded", "mod-neoforge", mc, modVer, loaded);

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
        //? if >=1.21.11 {
        buffers.endBatch(RenderTypes.lines());
        //?} else {
        /*buffers.endBatch(RenderType.lines());
        *///?}
    }
}
//?}
