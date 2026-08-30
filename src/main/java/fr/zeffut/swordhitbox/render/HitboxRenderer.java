package fr.zeffut.swordhitbox.render;

import fr.zeffut.swordhitbox.config.ModConfig;
import java.util.List;

//? if fabric {
//? if >=26.2 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.RenderPipelines;
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
//?} else if >=26.1 {
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
//? if >=1.21.11 {
import net.minecraft.client.render.RenderLayers;
//?} else {
/*import net.minecraft.client.render.RenderLayer;
*///?}
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
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
import net.minecraft.client.renderer.RenderType;
//?}
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
import net.minecraft.resources.ResourceLocation;
//?}
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
 * Client-side, purely visual hitbox overlay. While the local player holds a sword, every nearby
 * living entity gets its collision box outlined, with an optional eye-height band, an optional
 * look-direction segment, and an optional target recolor. Nothing is sent to the server.
 *
 * <p>Render APIs differ across MC versions:
 * <ul>
 *   <li><b>26.2+</b> (Mojmap): {@code LevelRenderEvents} + {@code SubmitNodeCollector.submitShapeOutline}
 *       (the new nodal render pipeline; ShapeRenderer/MultiBufferSource were removed).</li>
 *   <li><b>26.1</b> (Mojmap): {@code LevelRenderEvents} + {@code ShapeRenderer.renderShape}.</li>
 *   <li><b>1.21.10+</b> (Yarn): {@code WorldRenderEvents} + {@code VertexRendering.drawOutline}.</li>
 * </ul>
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
    //? if >=1.21.11 {
    /*private static final TagKey<Item> SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "swords"));
    private static final TagKey<Item> C_SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "swords"));
    *///?} else {
    /*private static final TagKey<Item> SWORDS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "swords"));
    private static final TagKey<Item> C_SWORDS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "swords"));
    *///?}
    //?}

    private HitboxRenderer() {}

    //? if fabric {
    //? if >=26.2 {
    public static void render(PoseStack pose, SubmitNodeCollector collector, Vec3 camPos, float tickProgress) {
        if (pose == null || collector == null || camPos == null) return;
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

        List<? extends Entity> entities;
        if (playersOnly) {
            List<? extends Player> players = level.players();
            entities = players.stream().filter(p -> p != player && p.isAlive()).toList();
        } else {
            entities = level.getEntities(player,
                    player.getBoundingBox().inflate(cfg.radius()),
                    e -> e.isAlive() && e instanceof LivingEntity);
        }
        for (Entity e : entities) {
            Vec3 lerp = e.getPosition(tickProgress);
            Vec3 delta = lerp.subtract(e.position());
            AABB box = e.getBoundingBox().move(delta);

            int color = (changeTarget && e == targeted) ? targetColor : boxColor;
            drawBox(pose, collector, box, camPos, color, lw);

            if (renderEye) {
                double eyeY = box.minY + e.getEyeHeight();
                drawBox(pose, collector, new AABB(box.minX, eyeY - EYE_HALF, box.minZ,
                        box.maxX, eyeY + EYE_HALF, box.maxZ), camPos, eyeColor, lw);
            }
            if (renderView) {
                Vec3 eye = lerp.add(0.0, e.getEyeHeight(), 0.0);
                Vec3 look = e.getViewVector(tickProgress);
                Vec3 end = eye.add(look.scale(VIEW_LEN));
                drawSegment(pose, collector, eye, end, camPos, viewColor, lw);
            }
        }
    }

    private static void drawBox(PoseStack pose, SubmitNodeCollector collector, AABB box, Vec3 cam, int argb, float lw) {
        VoxelShape shape = Shapes.create(box);
        collector.submitShapeOutline(pose, shape, RenderType.create("swordhitbox_lines", RenderSetup.builder(RenderPipelines.LINES).createRenderSetup()), argb, lw, false);
    }

    private static void drawSegment(PoseStack pose, SubmitNodeCollector collector, Vec3 a, Vec3 b, Vec3 cam, int argb, float lw) {
        AABB seg = new AABB(a.x, a.y, a.z, b.x, b.y, b.z);
        collector.submitShapeOutline(pose, Shapes.create(seg), RenderType.create("swordhitbox_lines", RenderSetup.builder(RenderPipelines.LINES).createRenderSetup()), argb, lw, false);
    }
    //?} else if >=26.1 {
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
        List<? extends Entity> entities;
        if (playersOnly) {
            List<? extends Player> players = level.players();
            entities = players.stream().filter(p -> p != player && p.isAlive()).toList();
        } else {
            entities = level.getEntities(player,
                    player.getBoundingBox().inflate(cfg.radius()),
                    e -> e.isAlive() && e instanceof LivingEntity);
        }
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

        //? if >=1.21.11 {
        VertexConsumer vc = buffers.getBuffer(RenderLayers.lines());
        //?} else {
        /*VertexConsumer vc = buffers.getBuffer(RenderLayer.getLines());
        *///?}
        List<? extends Entity> entities;
        if (playersOnly) {
            List<? extends PlayerEntity> players = world.getPlayers();
            entities = players.stream().filter(p -> p != player && p.isAlive()).toList();
        } else {
            entities = world.getOtherEntities(player,
                    player.getBoundingBox().expand(cfg.radius()),
                    e -> e.isAlive() && e instanceof LivingEntity);
        }
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
        //? if >=1.21.11 {
        VertexRendering.drawOutline(matrices, vc, shape, -cam.x, -cam.y, -cam.z, argb, lw);
        //?} else {
        /*VertexRendering.drawOutline(matrices, vc, shape, -cam.x, -cam.y, -cam.z, argb);
        *///?}
    }

    private static void drawSegment(MatrixStack matrices, VertexConsumer vc, Vec3d a, Vec3d b, Vec3d cam, int argb, float lw) {
        Box seg = new Box(a.x, a.y, a.z, b.x, b.y, b.z);
        //? if >=1.21.11 {
        VertexRendering.drawOutline(matrices, vc, VoxelShapes.cuboid(seg), -cam.x, -cam.y, -cam.z, argb, lw);
        //?} else {
        /*VertexRendering.drawOutline(matrices, vc, VoxelShapes.cuboid(seg), -cam.x, -cam.y, -cam.z, argb);
        *///?}
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

        //? if >=1.21.11 {
        VertexConsumer vc = buffers.getBuffer(RenderTypes.lines());
        //?} else {
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());
        //?}
        List<? extends Entity> entities;
        if (playersOnly) {
            List<? extends Player> players = level.players();
            entities = players.stream().filter(p -> p != player && p.isAlive()).toList();
        } else {
            entities = level.getEntities(player,
                    player.getBoundingBox().inflate(cfg.radius()),
                    e -> e.isAlive() && e instanceof LivingEntity);
        }
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
        //? if >=1.21.11 {
        ShapeRenderer.renderShape(pose, vc, shape, -cam.x, -cam.y, -cam.z, argb, lw);
        //?} else {
        ShapeRenderer.renderShape(pose, vc, shape, -cam.x, -cam.y, -cam.z, argb);
        //?}
    }

    private static void drawSegment(PoseStack pose, VertexConsumer vc, Vec3 a, Vec3 b, Vec3 cam, int argb, float lw) {
        AABB seg = new AABB(a.x, a.y, a.z, b.x, b.y, b.z);
        //? if >=1.21.11 {
        ShapeRenderer.renderShape(pose, vc, Shapes.create(seg), -cam.x, -cam.y, -cam.z, argb, lw);
        //?} else {
        ShapeRenderer.renderShape(pose, vc, Shapes.create(seg), -cam.x, -cam.y, -cam.z, argb);
        //?}
    }
    *///?}

    //? if fabric {
    //? if >=26.1 {
    /*private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?} else {
    private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.isIn(SWORDS) || stack.isIn(C_SWORDS));
    }
    //?}
    //?} else {
    /*private static boolean isSword(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORDS) || stack.is(C_SWORDS));
    }
    *///?}
}
