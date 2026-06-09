//? if neoforge {
package fr.zeffut.swordhitbox.neoforge;

import fr.zeffut.swordhitbox.config.ConfigScreen;
import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.hitbox.SwordHitboxToggle;
import fr.zeffut.swordhitbox.platform.Platform;
import fr.zeffut.swordhitbox.telemetry.Telemetry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NeoForge client entrypoint. Initializes config + telemetry, emits the standard
 * {@code client_started} / {@code mod_loaded} events plus a one-shot {@code swh_hitboxes_shown}
 * event, and subscribes a client-tick hook that toggles the native vanilla hitbox overlay
 * (F3+B) while a sword is held — see {@link SwordHitboxToggle}.
 */
@Mod(value = "swordhitbox", dist = Dist.CLIENT)
public class SwordHitboxNeoForge {
    private static final Logger LOG = LoggerFactory.getLogger("SwordHitbox");

    public SwordHitboxNeoForge(IEventBus modBus, ModContainer container) {
        // Touch config first so install_id / telemetry opt-out are resolved before any capture.
        ModConfig cfg = ModConfig.get();

        NeoForge.EVENT_BUS.addListener(
                (ClientTickEvent.Post event) -> SwordHitboxToggle.clientTick());

        // Native "Config" button in the NeoForge mod list, opening our vanilla ConfigScreen.
        // IConfigScreenFactory.createScreen(ModContainer, Screen) has the same signature on
        // 1.21.11 and 26.1.2, so no version gate is needed here. The mod is @Mod(dist = CLIENT),
        // so this constructor only runs client-side.
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (IConfigScreenFactory) (c, parent) -> new ConfigScreen(parent));

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

        Telemetry.captureModEvent("hitboxes_shown", "mod-neoforge", mc, modVer,
                Map.of("enabled", cfg.enabled()));

        LOG.info("[SwordHitbox] initialized on neoforge {} (telemetry={})", mc, Telemetry.enabled());
    }
}
//?}
