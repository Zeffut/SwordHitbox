//? if fabric {
package fr.zeffut.swordhitbox.fabric;

import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.hitbox.SwordHitboxToggle;
import fr.zeffut.swordhitbox.platform.Platform;
import fr.zeffut.swordhitbox.telemetry.Telemetry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric client entrypoint. Initializes config + telemetry, emits the standard
 * {@code client_started} / {@code mod_loaded} events plus a one-shot {@code swh_hitboxes_shown}
 * event, and registers a client-tick hook that toggles the native vanilla hitbox overlay
 * (F3+B) while a sword is held — see {@link SwordHitboxToggle}.
 */
public class SwordHitboxFabric implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("SwordHitbox");

    @Override
    public void onInitializeClient() {
        // Touch config first so install_id / telemetry opt-out are resolved before any capture.
        ModConfig cfg = ModConfig.get();

        ClientTickEvents.END_CLIENT_TICK.register(client -> SwordHitboxToggle.clientTick());

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

        Telemetry.captureModEvent("hitboxes_shown", "mod-fabric", mc, modVer,
                Map.of("enabled", cfg.enabled()));

        LOG.info("[SwordHitbox] initialized on fabric {} (telemetry={})", mc, Telemetry.enabled());
    }
}
//?}
