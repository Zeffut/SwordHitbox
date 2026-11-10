package fr.zeffut.swordhitbox;

import fr.zeffut.swordhitbox.config.ModConfig;
import fr.zeffut.swordhitbox.render.ModernHitboxRenderer;

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

    public void tick(Minecraft mc) {
            if (mc.player == null || mc.level == null) return;
            mc.player.getInventory().setSelectedSlot(0);
            if (checked) {
                mc.player.getInventory().setSelectedItem(new ItemStack(Items.DIAMOND_SWORD));
                return;
            }
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
