package fr.zeffut.swordhitbox;

import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.render.ModernHitboxRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.phys.AABB;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Opt-in integration probe; never included in production builds. */
public final class SmokeProbe {
    private boolean checked;
    private int rendered;

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError("SwordHitbox smoke: " + name);
        System.out.println("SWORDHITBOX_SMOKE_PASS " + name);
    }

    private static void checkSubmittedGeometry() {
        var box = new AABB(1, 2, 3, 4, 5, 6);
        var start = new Vec3(2, 3, 4);
        var end = new Vec3(3, 4, 5);
        var submitted = new ArrayList<String>();
        var vertices = new ArrayList<Vec3>();
        var colors = new ArrayList<Integer>();
        var widths = new ArrayList<Float>();
        VertexConsumer consumer = (VertexConsumer) Proxy.newProxyInstance(
                VertexConsumer.class.getClassLoader(), new Class<?>[] {VertexConsumer.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("addVertex") && args.length == 4)
                        vertices.add(new Vec3((float) args[1], (float) args[2], (float) args[3]));
                    if (method.getName().equals("setColor") && args.length == 1) colors.add((int) args[0]);
                    if (method.getName().equals("setLineWidth")) widths.add((float) args[0]);
                    return proxy;
                });
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class<?>[] {SubmitNodeCollector.class},
                (proxy, method, args) -> {
                    submitted.add(method.getName());
                    if (method.getName().equals("submitShapeOutline")) {
                        var shape = (net.minecraft.world.phys.shapes.VoxelShape) args[1];
                        check(shape.bounds().equals(box) && (int) args[3] == 0xff112233
                                && (float) args[4] == 2.0f, "submitted box geometry, color and width");
                    }
                    if (method.getName().equals("submitCustomGeometry"))
                        ((SubmitNodeCollector.CustomGeometryRenderer) args[2]).render(new PoseStack().last(), consumer);
                    return null;
                });
        ModernHitboxRenderer.submit(new PoseStack(), collector, List.of(
                new ModernHitboxRenderer.Outline(box, null, null, 0xff112233, 2.0f),
                new ModernHitboxRenderer.Outline(null, start, end, 0xff445566, 3.0f)));
        check(submitted.equals(List.of("submitShapeOutline", "submitCustomGeometry")),
                "box and segment use distinct submission methods");
        check(vertices.equals(List.of(start, end)), "submitted segment vertices match endpoints");
        check(colors.equals(List.of(0xff445566, 0xff445566)) && widths.equals(List.of(3.0f, 3.0f)),
                "submitted segment color and width");
    }

    public void tick(Minecraft mc) {
            if (mc.player == null || mc.level == null) return;
            mc.player.getInventory().setSelectedSlot(0);
            if (checked) {
                mc.player.getInventory().setSelectedItem(new ItemStack(Items.DIAMOND_SWORD));
                return;
            }
            checkSubmittedGeometry();
            ModConfig cfg = ModConfig.get();
            cfg.setTelemetry(false);
            cfg.setEnabled(true);
            cfg.setPlayersOnly(false);
            cfg.setRenderEyeBox(true);
            cfg.setRenderViewVector(true);
            cfg.setChangeTargetColor(true);
            cfg.setRadius(16);
            Vec3 camera = mc.player.getEyePosition();
            mc.player.getInventory().setSelectedItem(ItemStack.EMPTY);
            check(ModernHitboxRenderer.extract(camera, 1).isEmpty(), "no sword hides overlay");
            mc.player.getInventory().setSelectedItem(new ItemStack(Items.DIAMOND_SWORD));
            cfg.setEnabled(false);
            check(ModernHitboxRenderer.extract(camera, 1).isEmpty(), "disabled hides overlay");
            cfg.setEnabled(true);
            cfg.setPlayersOnly(true);
            check(ModernHitboxRenderer.extract(camera, 1).isEmpty(), "local player excluded");
            cfg.setPlayersOnly(false);
            Zombie zombie = new Zombie(EntityTypes.ZOMBIE, mc.level);
            zombie.setId(-1337);
            zombie.setPos(mc.player.position().add(0, 0, 3));
            mc.level.addEntity(zombie);
            mc.hitResult = new EntityHitResult(zombie);
            var outlines = ModernHitboxRenderer.extract(camera, 1);
            check(outlines.size() >= 3, "body eye band and view vector extracted");
            check(outlines.stream().anyMatch(o -> o.box() != null && o.color() == cfg.targetColor()),
                    "target color preserved");
            check(outlines.stream().anyMatch(o -> o.box() == null && o.start() != null
                    && Math.abs(o.end().distanceTo(o.start()) - 2) < 0.0001),
                    "view vector remains a two metre segment");
            try {
                outlines.clear();
                throw new AssertionError("Mutable extraction snapshot");
            } catch (UnsupportedOperationException expected) {
                check(true, "extraction snapshot immutable");
            }
            checked = true;
    }

    public void render() {
            if (checked && ++rendered == 60) {
                check(true, "60 live geometry submission frames without renderer failure");
                System.out.println("SWORDHITBOX_SMOKE_COMPLETE");
                Minecraft.getInstance().stop();
            }
    }
}
