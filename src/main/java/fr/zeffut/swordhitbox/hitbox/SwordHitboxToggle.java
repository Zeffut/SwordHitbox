package fr.zeffut.swordhitbox.hitbox;

import fr.zeffut.swordhitbox.config.ModConfig;

//? if fabric {
//? if >=26.1 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
*///?} else {
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
//?}
//?}
//? if neoforge {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
*///?}

/**
 * Drives Minecraft's <em>native</em> entity-hitbox debug rendering (the F3+B overlay) so that the
 * exact vanilla hitboxes — interpolated white box, red eye box, blue look vector — are shown for
 * every entity while the local player holds a sword. No custom geometry is drawn; we only flip the
 * built-in {@code ENTITY_HITBOXES} debug-screen entry to "always on".
 *
 * <p>In 1.21.11+ the legacy {@code EntityRenderDispatcher.setRenderHit(b/B)oxes(boolean)} flag was
 * removed: hitbox rendering is now one of the debug-screen entries, whose per-entry visibility
 * ({@code ALWAYS_ON} / {@code IN_OVERLAY} / {@code NEVER}) is held in a client profile. Forcing the
 * overlay on therefore means setting that entry's visibility to {@code ALWAYS_ON}.
 *
 * <p>Swords are detected via the {@code minecraft:swords} item tag (with a {@code c:swords}
 * fallback), covering vanilla, component-defined and modded swords — never {@code instanceof}.
 *
 * <p>The visibility is only touched on a <em>transition</em> (sword taken / sword dropped). When the
 * mod forces the overlay on, it first snapshots the user's existing visibility for that entry and
 * restores it on release, so a manually configured F3+B hitbox view (and the persisted profile) is
 * left exactly as the user had it.
 *
 * <p>Mapping-gated: Yarn on {@code fabric && <26.1}
 * ({@code MinecraftClient.debugHudEntryList} / {@code DebugHudProfile} /
 * {@code DebugHudEntryVisibility} / {@code DebugHudEntries.ENTITY_HITBOXES}), Mojmap everywhere else
 * ({@code Minecraft.debugEntries} / {@code DebugScreenEntryList} / {@code DebugScreenEntryStatus} /
 * {@code DebugScreenEntries.ENTITY_HITBOXES}).
 */
public final class SwordHitboxToggle {

    //? if fabric {
    //? if >=26.1 {
    /*private static final TagKey<Item> SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "swords"));
    private static final TagKey<Item> C_SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "swords"));
    *///?} else {
    private static final TagKey<Item> SWORDS =
            TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "swords"));
    private static final TagKey<Item> C_SWORDS =
            TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "swords"));
    //?}
    //?}
    //? if neoforge {
    /*private static final TagKey<Item> SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "swords"));
    private static final TagKey<Item> C_SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "swords"));
    *///?}

    /** True while the mod is the one forcing the hitbox overlay on. */
    private static boolean forcedByMod = false;

    //? if fabric {
    //? if >=26.1 {
    /*private static DebugScreenEntryStatus userStatus = null;
    *///?} else {
    private static DebugHudEntryVisibility userStatus = null;
    //?}
    //?}
    //? if neoforge {
    /*private static DebugScreenEntryStatus userStatus = null;
    *///?}

    private SwordHitboxToggle() {}

    /** Call once per client tick. Flips the native hitbox entry on sword take / release transitions. */
    public static void clientTick() {
        //? if fabric {
        //? if >=26.1 {
        /*Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        DebugScreenEntryList entries = client.debugEntries;
        if (player == null || entries == null) {
            if (forcedByMod && entries != null) {
                if (userStatus != null) entries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, userStatus);
                forcedByMod = false;
                userStatus = null;
            }
            return;
        }
        boolean swordHeld = ModConfig.get().enabled() && isSword(mainHand(player));
        if (swordHeld && !forcedByMod) {
            userStatus = entries.getStatus(DebugScreenEntries.ENTITY_HITBOXES);
            entries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, DebugScreenEntryStatus.ALWAYS_ON);
            forcedByMod = true;
        } else if (!swordHeld && forcedByMod) {
            if (userStatus != null) entries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, userStatus);
            forcedByMod = false;
            userStatus = null;
        }
        *///?} else {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        DebugHudProfile entries = client.debugHudEntryList;
        if (player == null || entries == null) {
            if (forcedByMod && entries != null) {
                if (userStatus != null) entries.setEntryVisibility(DebugHudEntries.ENTITY_HITBOXES, userStatus);
                forcedByMod = false;
                userStatus = null;
            }
            return;
        }
        boolean swordHeld = ModConfig.get().enabled() && isSword(mainHand(player));
        if (swordHeld && !forcedByMod) {
            userStatus = entries.getVisibility(DebugHudEntries.ENTITY_HITBOXES);
            entries.setEntryVisibility(DebugHudEntries.ENTITY_HITBOXES, DebugHudEntryVisibility.ALWAYS_ON);
            forcedByMod = true;
        } else if (!swordHeld && forcedByMod) {
            if (userStatus != null) entries.setEntryVisibility(DebugHudEntries.ENTITY_HITBOXES, userStatus);
            forcedByMod = false;
            userStatus = null;
        }
        //?}
        //?}
        //? if neoforge {
        /*Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        DebugScreenEntryList entries = client.debugEntries;
        if (player == null || entries == null) {
            if (forcedByMod && entries != null) {
                if (userStatus != null) entries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, userStatus);
                forcedByMod = false;
                userStatus = null;
            }
            return;
        }
        boolean swordHeld = ModConfig.get().enabled() && isSword(mainHand(player));
        if (swordHeld && !forcedByMod) {
            userStatus = entries.getStatus(DebugScreenEntries.ENTITY_HITBOXES);
            entries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, DebugScreenEntryStatus.ALWAYS_ON);
            forcedByMod = true;
        } else if (!swordHeld && forcedByMod) {
            if (userStatus != null) entries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, userStatus);
            forcedByMod = false;
            userStatus = null;
        }
        *///?}
    }

    //? if fabric {
    //? if >=26.1 {
    /*private static ItemStack mainHand(LocalPlayer player) {
        return player.getMainHandItem();
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?} else {
    private static ItemStack mainHand(ClientPlayerEntity player) {
        return player.getMainHandStack();
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.isIn(SWORDS) || stack.isIn(C_SWORDS));
    }
    //?}
    //?}
    //? if neoforge {
    /*private static ItemStack mainHand(LocalPlayer player) {
        return player.getMainHandItem();
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?}
}
