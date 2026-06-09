package fr.zeffut.swordhitbox.config;

//? if fabric {
//? if >=26.1 {
/*import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
*///?} else {
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
//?}
//?}
//? if neoforge {
/*import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
*///?}

/**
 * Minimal vanilla in-game config screen for SwordHitbox. No cloth-config, no extra GUI dependency:
 * just three {@code Button}s wired to {@link ModConfig}.
 *
 * <ul>
 *   <li><b>Show hitboxes: ON/OFF</b> — toggles {@link ModConfig#enabled()} / {@link ModConfig#setEnabled(boolean)};</li>
 *   <li><b>Telemetry: ON/OFF</b> — toggles {@link ModConfig#telemetry()} / {@link ModConfig#setTelemetry(boolean)};</li>
 *   <li><b>Done</b> — returns to {@code parent}.</li>
 * </ul>
 *
 * <p>Every toggle persists immediately (the {@code ModConfig} setters call {@code save()}), and the
 * hitbox toggle is read live each client tick by {@code SwordHitboxToggle}, so the change is visible
 * at once.
 *
 * <p>Mapping-gated: Yarn on {@code fabric && <26.1}
 * ({@code Screen} / {@code ButtonWidget.builder(...).dimensions(...).build()} / {@code addDrawableChild}
 * / {@code Text.literal} / {@code close()} / {@code this.client.setScreen}), Mojmap everywhere else
 * ({@code Screen} / {@code Button.builder(...).bounds(...).build()} / {@code addRenderableWidget}
 * / {@code Component.literal} / {@code onClose()} / {@code this.minecraft.setScreen}).
 *
 * <p>The screen intentionally does <em>not</em> override {@code render} / {@code extractRenderState}:
 * the base {@code Screen} already draws the default background and auto-renders the added widgets on
 * every supported version (the render pipeline diverges between 1.21.x and 26.1, so overriding it
 * would be the only fragile part — we avoid it entirely).
 */
public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        //? if fabric {
        //? if >=26.1 {
        /*super(Component.literal("SwordHitbox"));
        *///?} else {
        super(Text.literal("SwordHitbox"));
        //?}
        //?}
        //? if neoforge {
        /*super(Component.literal("SwordHitbox"));*/
        //?}
        this.parent = parent;
    }

    private static String hitboxLabel() {
        return "Show hitboxes: " + (ModConfig.get().enabled() ? "ON" : "OFF");
    }

    private static String telemetryLabel() {
        return "Telemetry: " + (ModConfig.get().telemetry() ? "ON" : "OFF");
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 30;

        //? if fabric {
        //? if >=26.1 {
        /*addRenderableWidget(Button.builder(
                Component.literal(hitboxLabel()),
                btn -> {
                    ModConfig.get().setEnabled(!ModConfig.get().enabled());
                    btn.setMessage(Component.literal(hitboxLabel()));
                })
                .bounds(x, y, 200, 20)
                .build());

        addRenderableWidget(Button.builder(
                Component.literal(telemetryLabel()),
                btn -> {
                    ModConfig.get().setTelemetry(!ModConfig.get().telemetry());
                    btn.setMessage(Component.literal(telemetryLabel()));
                })
                .bounds(x, y + 24, 200, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> back())
                .bounds(x, y + 56, 200, 20)
                .build());
        *///?} else {
        addDrawableChild(ButtonWidget.builder(
                Text.literal(hitboxLabel()),
                btn -> {
                    ModConfig.get().setEnabled(!ModConfig.get().enabled());
                    btn.setMessage(Text.literal(hitboxLabel()));
                })
                .dimensions(x, y, 200, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal(telemetryLabel()),
                btn -> {
                    ModConfig.get().setTelemetry(!ModConfig.get().telemetry());
                    btn.setMessage(Text.literal(telemetryLabel()));
                })
                .dimensions(x, y + 24, 200, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> back())
                .dimensions(x, y + 56, 200, 20)
                .build());
        //?}
        //?}
        //? if neoforge {
        /*addRenderableWidget(Button.builder(
                Component.literal(hitboxLabel()),
                btn -> {
                    ModConfig.get().setEnabled(!ModConfig.get().enabled());
                    btn.setMessage(Component.literal(hitboxLabel()));
                })
                .bounds(x, y, 200, 20)
                .build());

        addRenderableWidget(Button.builder(
                Component.literal(telemetryLabel()),
                btn -> {
                    ModConfig.get().setTelemetry(!ModConfig.get().telemetry());
                    btn.setMessage(Component.literal(telemetryLabel()));
                })
                .bounds(x, y + 24, 200, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> back())
                .bounds(x, y + 56, 200, 20)
                .build());
        *///?}
    }

    /** Returns to the parent screen. Mapping-agnostic helper used by the Done button. */
    private void back() {
        //? if fabric {
        //? if >=26.1 {
        /*if (this.minecraft != null) this.minecraft.setScreen(parent);
        *///?} else {
        if (this.client != null) this.client.setScreen(parent);
        //?}
        //?}
        //? if neoforge {
        /*if (this.minecraft != null) this.minecraft.setScreen(parent);*/
        //?}
    }

    // Vanilla "close" hook (Esc). Yarn names it close(); Mojmap names it onClose().
    //? if fabric {
    //? if >=26.1 {
    /*@Override
    public void onClose() { back(); }
    *///?} else {
    @Override
    public void close() { back(); }
    //?}
    //?}
    //? if neoforge {
    /*@Override
    public void onClose() { back(); }*/
    //?}
}
