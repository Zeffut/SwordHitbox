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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
*///?}

/**
 * Client-side, purely visual hitbox overlay modeled on the vanilla F3+B debug rendering and on the
 * "combat-hitboxes" mod: while the local player holds a sword, every nearby living entity gets its
 * collision box outlined, with an optional eye-height band, an optional look-direction segment, and
 * an optional target recolor. Nothing is sent to the server (local rendering only), so it works on
 * every server and changes no gameplay.
 *
 * <p><b>Interpolation (the key fidelity fix):</b> entities are drawn at their <em>interpolated</em>
 * render position, not their raw tick position. We compute the lerp delta
 * {@code lerp = entity.getPosition(tickProgress) - entity.position()} (Mojmap) /
 * {@code entity.getLerpedPos(tickDelta) - entity.getPos()} (Yarn) and offset the bounding box by it,
 * so the overlay tracks moving entities smoothly instead of lagging a tick behind.
 *
 * <p>Swords are detected via the {@code minecraft:swords} item tag (with a {@code c:swords}
 * fallback) so it covers vanilla, component-defined and modded swords — never {@code instanceof}.
 *
 * <p>The eye band and the look-direction segment are drawn as thin axis-aligned boxes through the
 * same outline API used for the main box; this keeps the renderer to a single, version-robust
 * drawing primitive across all four Stonecutter nodes (Yarn / Mojmap, Fabric / NeoForge).
 *
 * <p>Mapping-gated: Yarn on {@code fabric && <26.1}
 * ({@code VertexRendering.drawOutline} / {@code VoxelShapes.cuboid} / {@code getLerpedPos} /
 * {@code getStandingEyeHeight} / {@code getRotationVec}), Mojmap everywhere else
 * ({@code ShapeRenderer.renderShape} / {@code Shapes.create} / {@code getPosition} /
 * {@code getEyeHeight} / {@code getViewVector}).
 */
public final class HitboxRenderer {

    private static final double VIEW_LEN = 2.0;
    private static final double EYE_HALF = 0.01;

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
    /*public static void render(PoseStack pose, MultiBufferSource buffers, Vec3 camPos, float tickProgress) {
        if (pose == null || buffers == null || camPos == null) return;
        ModConfig cfg = ModConfig.get();
        if (!cfg.enabled()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;
        if (!isSword(player.getMainHandItem())) return;

        int boxColor = cfg.boxColor();
        int eyeColor = cfg.eyeColor();
        int viewColor = cfg.viewColor();
        int targetColor = cfg.targetColor();
        boolean changeTarget = cfg.changeTargetColor();
        boolean renderEye = cfg.renderEyeBox();
        boolean renderView = cfg.renderViewVector();
        boolean playersOnly = cfg.playersOnly();
        float lw = cfg.lineWidth();

        Entity targeted = null;
        HitResult hit = mc.hitResult;
        if (changeTarget && hit instanceof EntityHitResult ehr) targeted = ehr.getEntity();

        VertexConsumer vc = buffers.getBuffer(RenderTypes.lines());
        List<Entity> entities = level.getEntities(player,
                player.getBoundingBox().inflate(cfg.radius()),
                e -> e.isAlive() && (playersOnly ? e instanceof Player : e instanceof LivingEntity));
        for (Entity e : entities) {
            Vec3 lerp = e.getPosition(tickProgress);
            Vec3 delta = lerp.subtract(e.position());
            AABB box = e.getBoundingBox().move(delta);

            int color = (changeTarget && e == targeted) ? targetColor : boxColor;
            drawBox(pose, vc, box, camPos, color, lw);

            if (renderEye) {
                double eyeY = box.minY + e.getEyeHeight();
                drawBox(pose, vc, new AABB(box.minX, eyeY - EYE_HALF, box.minZ,
                        box.maxX, eyeY + EYE_HALF, box.maxZ), camPos, eyeColor, lw);
            }
            if (renderView) {
                Vec3 eye = lerp.add(0.0, e.getEyeHeight(), 0.0);
                Vec3 look = e.getViewVector(tickProgress);
                Vec3 end = eye.add(look.scale(VIEW_LEN));
                drawSegment(pose, vc, eye, end, camPos, viewColor, lw);
            }
        }
    }

    private static void drawBox(PoseStack pose, VertexConsumer vc, AABB box, Vec3 cam, int argb, float lw) {
        VoxelShape shape = Shapes.create(box);
        ShapeRenderer.renderShape(pose, vc, shape, -cam.x, -cam.y, -cam.z, argb, lw);
    }

    private static void drawSegment(PoseStack pose, VertexConsumer vc, Vec3 a, Vec3 b, Vec3 cam, int argb, float lw) {
        AABB seg = new AABB(a.x, a.y, a.z, b.x, b.y, b.z);
        ShapeRenderer.renderShape(pose, vc, Shapes.create(seg), -cam.x, -cam.y, -cam.z, argb, lw);
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?} else {
    public static void render(MatrixStack matrices, VertexConsumerProvider buffers, Vec3d camPos, float tickDelta) {
        if (matrices == null || buffers == null || camPos == null) return;
        ModConfig cfg = ModConfig.get();
        if (!cfg.enabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;
        if (player == null || world == null) return;
        if (!isSword(player.getMainHandStack())) return;

        int boxColor = cfg.boxColor();
        int eyeColor = cfg.eyeColor();
        int viewColor = cfg.viewColor();
        int targetColor = cfg.targetColor();
        boolean changeTarget = cfg.changeTargetColor();
        boolean renderEye = cfg.renderEyeBox();
        boolean renderView = cfg.renderViewVector();
        boolean playersOnly = cfg.playersOnly();
        float lw = cfg.lineWidth();

        Entity targeted = null;
        HitResult hit = mc.crosshairTarget;
        if (changeTarget && hit instanceof EntityHitResult ehr) targeted = ehr.getEntity();

        VertexConsumer vc = buffers.getBuffer(RenderLayers.lines());
        List<Entity> entities = world.getOtherEntities(player,
                player.getBoundingBox().expand(cfg.radius()),
                e -> e.isAlive() && (playersOnly ? e instanceof PlayerEntity : e instanceof LivingEntity));
        for (Entity e : entities) {
            Vec3d lerp = e.getLerpedPos(tickDelta);
            Vec3d delta = lerp.subtract(new Vec3d(e.getX(), e.getY(), e.getZ()));
            Box box = e.getBoundingBox().offset(delta);

            int color = (changeTarget && e == targeted) ? targetColor : boxColor;
            drawBox(matrices, vc, box, camPos, color, lw);

            if (renderEye) {
                double eyeY = box.minY + e.getStandingEyeHeight();
                drawBox(matrices, vc, new Box(box.minX, eyeY - EYE_HALF, box.minZ,
                        box.maxX, eyeY + EYE_HALF, box.maxZ), camPos, eyeColor, lw);
            }
            if (renderView) {
                Vec3d eye = lerp.add(0.0, e.getStandingEyeHeight(), 0.0);
                Vec3d look = e.getRotationVec(tickDelta);
                Vec3d end = eye.add(look.multiply(VIEW_LEN));
                drawSegment(matrices, vc, eye, end, camPos, viewColor, lw);
            }
        }
    }

    private static void drawBox(MatrixStack matrices, VertexConsumer vc, Box box, Vec3d cam, int argb, float lw) {
        VoxelShape shape = VoxelShapes.cuboid(box);
        VertexRendering.drawOutline(matrices, vc, shape, -cam.x, -cam.y, -cam.z, argb, lw);
    }

    private static void drawSegment(MatrixStack matrices, VertexConsumer vc, Vec3d a, Vec3d b, Vec3d cam, int argb, float lw) {
        Box seg = new Box(a.x, a.y, a.z, b.x, b.y, b.z);
        VertexRendering.drawOutline(matrices, vc, VoxelShapes.cuboid(seg), -cam.x, -cam.y, -cam.z, argb, lw);
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.isIn(SWORDS) || stack.isIn(C_SWORDS));
    }
    //?}
    //?}
    //? if neoforge {
    /*public static void render(PoseStack pose, MultiBufferSource buffers, Vec3 camPos, float tickProgress) {
        if (pose == null || buffers == null || camPos == null) return;
        ModConfig cfg = ModConfig.get();
        if (!cfg.enabled()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;
        if (!isSword(player.getMainHandItem())) return;

        int boxColor = cfg.boxColor();
        int eyeColor = cfg.eyeColor();
        int viewColor = cfg.viewColor();
        int targetColor = cfg.targetColor();
        boolean changeTarget = cfg.changeTargetColor();
        boolean renderEye = cfg.renderEyeBox();
        boolean renderView = cfg.renderViewVector();
        boolean playersOnly = cfg.playersOnly();
        float lw = cfg.lineWidth();

        Entity targeted = null;
        HitResult hit = mc.hitResult;
        if (changeTarget && hit instanceof EntityHitResult ehr) targeted = ehr.getEntity();

        VertexConsumer vc = buffers.getBuffer(RenderTypes.lines());
        List<Entity> entities = level.getEntities(player,
                player.getBoundingBox().inflate(cfg.radius()),
                e -> e.isAlive() && (playersOnly ? e instanceof Player : e instanceof LivingEntity));
        for (Entity e : entities) {
            Vec3 lerp = e.getPosition(tickProgress);
            Vec3 delta = lerp.subtract(e.position());
            AABB box = e.getBoundingBox().move(delta);

            int color = (changeTarget && e == targeted) ? targetColor : boxColor;
            drawBox(pose, vc, box, camPos, color, lw);

            if (renderEye) {
                double eyeY = box.minY + e.getEyeHeight();
                drawBox(pose, vc, new AABB(box.minX, eyeY - EYE_HALF, box.minZ,
                        box.maxX, eyeY + EYE_HALF, box.maxZ), camPos, eyeColor, lw);
            }
            if (renderView) {
                Vec3 eye = lerp.add(0.0, e.getEyeHeight(), 0.0);
                Vec3 look = e.getViewVector(tickProgress);
                Vec3 end = eye.add(look.scale(VIEW_LEN));
                drawSegment(pose, vc, eye, end, camPos, viewColor, lw);
            }
        }
    }

    private static void drawBox(PoseStack pose, VertexConsumer vc, AABB box, Vec3 cam, int argb, float lw) {
        VoxelShape shape = Shapes.create(box);
        ShapeRenderer.renderShape(pose, vc, shape, -cam.x, -cam.y, -cam.z, argb, lw);
    }

    private static void drawSegment(PoseStack pose, VertexConsumer vc, Vec3 a, Vec3 b, Vec3 cam, int argb, float lw) {
        AABB seg = new AABB(a.x, a.y, a.z, b.x, b.y, b.z);
        ShapeRenderer.renderShape(pose, vc, Shapes.create(seg), -cam.x, -cam.y, -cam.z, argb, lw);
    }

    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?}
}
