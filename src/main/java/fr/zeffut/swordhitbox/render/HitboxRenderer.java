package fr.zeffut.swordhitbox.render;

import fr.zeffut.swordhitbox.config.ModConfig;
import java.util.List;

//? if fabric {
//? if >=26.1 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
*///?} else {
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.client.util.math.MatrixStack;
//?}
//?}
//? if neoforge {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
*///?}

/**
 * Client-side, purely visual hitbox overlay (modeled on the "Mace Hitboxes" mod): while the local
 * player holds a sword, the collision box of every nearby living entity is outlined. Nothing is sent
 * to the server — this is local rendering only, so it works on every server and changes no gameplay.
 *
 * <p>Swords are detected via the {@code minecraft:swords} item tag (with a {@code c:swords}
 * fallback) so it covers vanilla, component-defined and modded swords — never {@code instanceof}.
 *
 * <p>Optionally (config {@code highlight_in_range}, off by default), entities within vanilla attack
 * range are drawn in {@code in_range_color} instead of {@code box_color}. The in-range test uses the
 * vanilla anti-cheat helper, not a custom distance calculation.
 *
 * <p>This class is mapping-gated: Yarn on {@code fabric && <26.1}, Mojmap everywhere else.
 */
public final class HitboxRenderer {

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

    private HitboxRenderer() {}

    //? if fabric {
    //? if >=26.1 {
    /*public static void render(PoseStack pose, MultiBufferSource buffers, Vec3 camPos) {
        if (pose == null || buffers == null || camPos == null) return;
        ModConfig cfg = ModConfig.get();
        if (!cfg.enabled()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;
        if (!isSword(player.getMainHandItem())) return;

        boolean highlight = cfg.highlightInRange();
        int boxColor = cfg.boxColor();
        int inRangeColor = cfg.inRangeColor();
        double radius = cfg.radius();

        VertexConsumer vc = buffers.getBuffer(RenderTypes.lines());
        List<Entity> entities = level.getEntities(player,
                player.getBoundingBox().inflate(radius),
                e -> e instanceof LivingEntity && e.isAlive());
        for (Entity e : entities) {
            int color = (highlight && player.isWithinEntityInteractionRange(e, 0.0)) ? inRangeColor : boxColor;
            drawBox(pose, vc, e.getBoundingBox(), camPos.x, camPos.y, camPos.z, color);
        }
    }

    private static void drawBox(PoseStack pose, VertexConsumer vc, AABB box,
                                double camX, double camY, double camZ, int argb) {
        VoxelShape shape = Shapes.create(box);
        ShapeRenderer.renderShape(pose, vc, shape, -camX, -camY, -camZ, argb, 1.0f);
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?} else {
    public static void render(MatrixStack matrices, VertexConsumerProvider buffers, Vec3d camPos) {
        if (matrices == null || buffers == null || camPos == null) return;
        ModConfig cfg = ModConfig.get();
        if (!cfg.enabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;
        if (player == null || world == null) return;
        if (!isSword(player.getMainHandStack())) return;

        boolean highlight = cfg.highlightInRange();
        int boxColor = cfg.boxColor();
        int inRangeColor = cfg.inRangeColor();
        double radius = cfg.radius();

        VertexConsumer vc = buffers.getBuffer(RenderLayers.lines());
        List<Entity> entities = world.getOtherEntities(player,
                player.getBoundingBox().expand(radius),
                e -> e instanceof LivingEntity && e.isAlive());
        for (Entity e : entities) {
            int color = (highlight && player.canInteractWithEntity(e, 0.0)) ? inRangeColor : boxColor;
            drawBox(matrices, vc, e.getBoundingBox(), camPos.x, camPos.y, camPos.z, color);
        }
    }

    private static void drawBox(MatrixStack matrices, VertexConsumer vc, Box box,
                                double camX, double camY, double camZ, int argb) {
        VoxelShape shape = VoxelShapes.cuboid(box);
        VertexRendering.drawOutline(matrices, vc, shape, -camX, -camY, -camZ, argb, 1.0f);
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.isIn(SWORDS) || stack.isIn(C_SWORDS));
    }
    //?}
    //?}
    //? if neoforge {
    /*public static void render(PoseStack pose, MultiBufferSource buffers, Vec3 camPos) {
        if (pose == null || buffers == null || camPos == null) return;
        ModConfig cfg = ModConfig.get();
        if (!cfg.enabled()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;
        if (!isSword(player.getMainHandItem())) return;

        boolean highlight = cfg.highlightInRange();
        int boxColor = cfg.boxColor();
        int inRangeColor = cfg.inRangeColor();
        double radius = cfg.radius();

        VertexConsumer vc = buffers.getBuffer(RenderTypes.lines());
        List<Entity> entities = level.getEntities(player,
                player.getBoundingBox().inflate(radius),
                e -> e instanceof LivingEntity && e.isAlive());
        for (Entity e : entities) {
            int color = (highlight && player.isWithinEntityInteractionRange(e, 0.0)) ? inRangeColor : boxColor;
            drawBox(pose, vc, e.getBoundingBox(), camPos.x, camPos.y, camPos.z, color);
        }
    }

    private static void drawBox(PoseStack pose, VertexConsumer vc, AABB box,
                                double camX, double camY, double camZ, int argb) {
        VoxelShape shape = Shapes.create(box);
        ShapeRenderer.renderShape(pose, vc, shape, -camX, -camY, -camZ, argb, 1.0f);
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?}
}
