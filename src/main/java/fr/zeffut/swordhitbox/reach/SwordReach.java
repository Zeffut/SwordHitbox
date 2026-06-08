package fr.zeffut.swordhitbox.reach;

import fr.zeffut.swordhitbox.compat.Compat;
import fr.zeffut.swordhitbox.config.ModConfig;

//? if fabric {
//? if >=26.1 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
*///?} else {
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
//?}
//?}
//? if neoforge {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
*///?}

/**
 * Core, mapping-gated logic that grows the player's entity-interaction range while a sword is held.
 *
 * <p>Applies a transient {@code ADD_VALUE} modifier (keyed by the {@code swordhitbox:sword_reach}
 * id, NOT a UUID — the UUID system was removed in 1.21) onto the local player's
 * {@code minecraft:player.entity_interaction_range} attribute. The modifier is only added/removed on
 * an actual state change, tracked by {@link #applied}, so the per-client-tick driver never spams the
 * attribute instance.
 *
 * <p>Sword detection uses the {@code minecraft:swords} item tag (with a {@code c:swords} fallback)
 * so it catches vanilla swords, component-defined swords, and modded swords alike — never
 * {@code instanceof SwordItem}.
 */
public final class SwordReach {

    private static final Identifier MODIFIER_ID = Compat.id("swordhitbox", "sword_reach");

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

    /** Whether our modifier is currently attached to the local player's attribute instance. */
    private static boolean applied = false;

    private SwordReach() {}

    /** Drives the modifier from the per-client tick. Safe to call when no player/world is present. */
    public static void clientTick() {
        ModConfig cfg = ModConfig.get();

        //? if fabric {
        //? if >=26.1 {
        /*LocalPlayer player = Minecraft.getInstance().player;
        *///?} else {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        //?}
        //?}
        //? if neoforge {
        /*LocalPlayer player = Minecraft.getInstance().player;
        *///?}
        if (player == null) {
            applied = false; // attribute instance is gone with the player; reset our bookkeeping.
            return;
        }

        if (!cfg.enabled()) {
            if (applied) removeModifier(player);
            return;
        }

        boolean wantSword = isSword(heldMainHand(player));
        if (wantSword && !applied) {
            addModifier(player, cfg.reachBonus());
        } else if (!wantSword && applied) {
            removeModifier(player);
        }
    }

    //? if fabric {
    //? if >=26.1 {
    /*private static ItemStack heldMainHand(LocalPlayer player) {
        return player.getMainHandItem();
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }

    private static void addModifier(LocalPlayer player, double bonus) {
        AttributeInstance inst = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (inst == null) return;
        inst.removeModifier(MODIFIER_ID);
        inst.addTransientModifier(new AttributeModifier(
                MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        applied = true;
    }

    private static void removeModifier(LocalPlayer player) {
        AttributeInstance inst = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (inst != null) inst.removeModifier(MODIFIER_ID);
        applied = false;
    }
    *///?} else {
    private static ItemStack heldMainHand(ClientPlayerEntity player) {
        return player.getMainHandStack();
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.isIn(SWORDS) || stack.isIn(C_SWORDS));
    }

    private static void addModifier(ClientPlayerEntity player, double bonus) {
        EntityAttributeInstance inst = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (inst == null) return;
        inst.removeModifier(MODIFIER_ID);
        inst.addTemporaryModifier(new EntityAttributeModifier(
                MODIFIER_ID, bonus, EntityAttributeModifier.Operation.ADD_VALUE));
        applied = true;
    }

    private static void removeModifier(ClientPlayerEntity player) {
        EntityAttributeInstance inst = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (inst != null) inst.removeModifier(MODIFIER_ID);
        applied = false;
    }
    //?}
    //?}
    //? if neoforge {
    /*private static ItemStack heldMainHand(LocalPlayer player) {
        return player.getMainHandItem();
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }

    private static void addModifier(LocalPlayer player, double bonus) {
        AttributeInstance inst = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (inst == null) return;
        inst.removeModifier(MODIFIER_ID);
        inst.addTransientModifier(new AttributeModifier(
                MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        applied = true;
    }

    private static void removeModifier(LocalPlayer player) {
        AttributeInstance inst = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (inst != null) inst.removeModifier(MODIFIER_ID);
        applied = false;
    }
    *///?}
}
