//? if fabric {
//? if <26.1 {
package fr.zeffut.swordhitbox.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu entrypoint: makes the "Config" button next to SwordHitbox in ModMenu open the shared
 * Cloth Config screen ({@link fr.zeffut.swordhitbox.config.ConfigScreenFactory}). Declared in
 * {@code fabric.mod.json} under the {@code "modmenu"} entrypoint.
 *
 * <p>Gated to {@code fabric && <26.1}: ModMenu has no release compatible with MC 26.1.2 (17.0.0
 * targets {@code >=1.21.11 <26}; 18.0.0-alpha targets {@code >1.26}). On the 26.1.2-fabric node the
 * ModMenu dependency and this entrypoint are dropped; the Cloth screen is still reachable on
 * NeoForge (and the Cloth lib is still present at runtime on Fabric 26.1.2).
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> fr.zeffut.swordhitbox.config.ConfigScreenFactory.create(parent);
    }
}
//?}
//?}
