//? if fabric {
//? if <26.1 {
package fr.zeffut.swordhitbox.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fr.zeffut.swordhitbox.config.ConfigScreen;

/**
 * ModMenu entrypoint: makes the "Config" button next to SwordHitbox in ModMenu open
 * {@link ConfigScreen}. Declared in {@code fabric.mod.json} under the {@code "modmenu"} entrypoint.
 *
 * <p>Gated to {@code fabric && <26.1}: ModMenu has no release for MC 26.1.2 (latest is 17.0.0 for
 * {@code >=1.21.11 <26}; 18.x targets {@code >1.26}, 19.x targets {@code >=26.2}). The dependency is
 * {@code modCompileOnly} + {@code modLocalRuntime}, so the mod still loads fine without ModMenu.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
//?}
//?}
