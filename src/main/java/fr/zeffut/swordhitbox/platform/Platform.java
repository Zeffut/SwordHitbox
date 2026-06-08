package fr.zeffut.swordhitbox.platform;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?}
//? if neoforge {
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
//?}

/**
 * Tiny loader-abstraction layer (no Architectury). Each accessor is implemented twice, gated by
 * Stonecutter constants so only the active loader's branch survives into the generated sources.
 *
 * <p>Provides: loader name, mod version, MC version, installed-mod count. The {@code swordhitbox}
 * literal below is rewritten by /mc-mod to the real mod id.
 */
public final class Platform {

    private Platform() {}

    /** "fabric" or "neoforge". */
    public static String loader() {
        //? if fabric {
        return "fabric";
        //?}
        //? if neoforge {
        /*return "neoforge";*/
        //?}
    }

    /** Friendly version string of this mod, or "unknown". */
    public static String modVersion() {
        //? if fabric {
        return FabricLoader.getInstance().getModContainer("swordhitbox")
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
        //?}
        //? if neoforge {
        /*return versionOf("swordhitbox");*/
        //?}
    }

    /** Friendly version string of Minecraft, or "unknown". */
    public static String mcVersion() {
        //? if fabric {
        return FabricLoader.getInstance().getModContainer("minecraft")
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
        //?}
        //? if neoforge {
        /*return versionOf("minecraft");*/
        //?}
    }

    /** Number of installed mods/containers. */
    public static int installedModCount() {
        //? if fabric {
        return FabricLoader.getInstance().getAllMods().size();
        //?}
        //? if neoforge {
        /*return ModList.get().size();*/
        //?}
    }

    /** True if running in a development environment. */
    public static boolean isDevelopment() {
        //? if fabric {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
        //?}
        //? if neoforge {
        /*return !net.neoforged.fml.loading.FMLEnvironment.isProduction();*/
        //?}
    }

    //? if neoforge {
    /*private static String versionOf(String modId) {
        for (IModInfo mod : ModList.get().getMods()) {
            if (modId.equals(mod.getModId())) return mod.getVersion().toString();
        }
        return "unknown";
    }*/
    //?}
}
