//? if >=26.3 {
package fr.zeffut.swordhitbox.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.zeffut.swordhitbox.config.ModConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

/** 26.3 render pipeline: extract immutable camera-relative geometry, then submit it. */
public final class ModernHitboxRenderer {
    private static final TagKey<Item> SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "swords"));
    private static final TagKey<Item> C_SWORDS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "swords"));

    /** No entity, world or mutable configuration may cross the extraction/render boundary. */
    public record Outline(AABB box, Vec3 start, Vec3 end, int color, float width) {}

    private ModernHitboxRenderer() {}

    public static List<Outline> extract(Vec3 camera, float partialTick) {
        ModConfig cfg = ModConfig.get();
        Minecraft mc = Minecraft.getInstance();
        if (!cfg.enabled() || mc.player == null || mc.level == null) return List.of();
        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || !(held.is(SWORDS) || held.is(C_SWORDS))) return List.of();

        Entity target = cfg.changeTargetColor() && mc.hitResult instanceof EntityHitResult hit
                ? hit.getEntity() : null;
        List<Outline> outlines = new ArrayList<>();
        List<? extends Entity> entities = cfg.playersOnly()
                ? mc.level.players().stream().filter(p -> p != mc.player && p.isAlive()).toList()
                : mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(cfg.radius()),
                        e -> e.isAlive() && e instanceof LivingEntity);
        for (Entity entity : entities) {
            Vec3 position = entity.getPosition(partialTick);
            AABB box = entity.getBoundingBox().move(position.subtract(entity.position()));
            int color = entity == target ? cfg.targetColor() : cfg.boxColor();
            outlines.add(new Outline(box.move(-camera.x, -camera.y, -camera.z), null, null,
                    color, cfg.lineWidth()));
            if (cfg.renderEyeBox()) {
                double eyeY = box.minY + entity.getEyeHeight();
                AABB eyes = new AABB(box.minX, eyeY - 0.01, box.minZ,
                        box.maxX, eyeY + 0.01, box.maxZ);
                outlines.add(new Outline(eyes.move(-camera.x, -camera.y, -camera.z), null, null,
                        cfg.eyeColor(), cfg.lineWidth()));
            }
            if (cfg.renderViewVector()) {
                Vec3 start = position.add(0.0, entity.getEyeHeight(), 0.0).subtract(camera);
                Vec3 end = start.add(entity.getViewVector(partialTick).scale(2.0));
                outlines.add(new Outline(null, start, end, cfg.viewColor(), cfg.lineWidth()));
            }
        }
        return List.copyOf(outlines);
    }

    public static void submit(PoseStack pose, SubmitNodeCollector collector, List<Outline> outlines) {
        if (pose == null || collector == null || outlines == null) return;
        for (Outline outline : outlines) {
            if (outline.box() != null) {
                collector.submitShapeOutline(pose, Shapes.create(outline.box()), RenderTypes.lines(),
                        outline.color(), outline.width(), false);
            } else {
                // A look vector is a line, not the bounding box of its two endpoints.
                collector.submitCustomGeometry(pose, RenderTypes.lines(), (matrix, vertices) -> {
                    Vec3 a = outline.start();
                    Vec3 b = outline.end();
                    Vec3 normal = b.subtract(a).normalize();
                    vertices.addVertex(matrix, (float) a.x, (float) a.y, (float) a.z)
                            .setColor(outline.color()).setNormal(matrix,
                                    (float) normal.x, (float) normal.y, (float) normal.z)
                            .setLineWidth(outline.width());
                    vertices.addVertex(matrix, (float) b.x, (float) b.y, (float) b.z)
                            .setColor(outline.color()).setNormal(matrix,
                                    (float) normal.x, (float) normal.y, (float) normal.z)
                            .setLineWidth(outline.width());
                });
            }
        }
    }
}
//?}
