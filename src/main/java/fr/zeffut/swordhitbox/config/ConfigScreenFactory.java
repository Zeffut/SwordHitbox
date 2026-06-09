package fr.zeffut.swordhitbox.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

//? if fabric {
//? if >=26.1 {
/*import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
*///?} else {
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
//?}
//?}
//? if neoforge {
/*import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
*///?}

/**
 * Builds the shared Cloth Config screen for SwordHitbox, used by both loaders:
 * <ul>
 *   <li><b>Fabric</b> via the {@code modmenu} entrypoint ({@code fabric.ModMenuIntegration});</li>
 *   <li><b>NeoForge</b> via {@code IConfigScreenFactory} registered in the mod constructor.</li>
 * </ul>
 *
 * <p>Categories: <b>Behavior</b> (boolean toggles), <b>Colors</b> (ARGB alpha color fields),
 * <b>Line</b> (float line width + double scan radius). Each entry's {@code setSaveConsumer} writes
 * back to {@link ModConfig}, and {@code setSavingRunnable} persists the whole config on apply.
 *
 * <p>Cloth Config exposes the same {@code me.shedaniel.clothconfig2.api.*} surface on Fabric and
 * NeoForge, so this builder is loader-agnostic; only the {@code Screen}/text type differs by mapping
 * (Yarn {@code Text} on {@code fabric && <26.1}, Mojmap {@code Component} elsewhere) and is gated
 * via a tiny {@code text(...)} / {@code title()} helper.
 */
public final class ConfigScreenFactory {

    private ConfigScreenFactory() {}

    public static Screen create(Screen parent) {
        ModConfig cfg = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(text("SwordHitbox"))
                .setSavingRunnable(cfg::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        // ---- Behavior --------------------------------------------------------------------
        ConfigCategory behavior = builder.getOrCreateCategory(text("Behavior"));

        behavior.addEntry(eb.startBooleanToggle(text("Enabled"), cfg.enabled())
                .setDefaultValue(ModConfig.DEF_ENABLED)
                .setTooltip(text("Draw hitboxes while holding a sword"))
                .setSaveConsumer(cfg::setEnabled)
                .build());

        behavior.addEntry(eb.startBooleanToggle(text("Render Eye Box"), cfg.renderEyeBox())
                .setDefaultValue(ModConfig.DEF_RENDER_EYE_BOX)
                .setTooltip(text("Thin band drawn at the entity's eye height"))
                .setSaveConsumer(cfg::setRenderEyeBox)
                .build());

        behavior.addEntry(eb.startBooleanToggle(text("Render View Vector"), cfg.renderViewVector())
                .setDefaultValue(ModConfig.DEF_RENDER_VIEW_VECTOR)
                .setTooltip(text("Segment indicating the direction the entity is facing"))
                .setSaveConsumer(cfg::setRenderViewVector)
                .build());

        behavior.addEntry(eb.startBooleanToggle(text("Change Target Color"), cfg.changeTargetColor())
                .setDefaultValue(ModConfig.DEF_CHANGE_TARGET_COLOR)
                .setTooltip(text("Recolor the box of the entity under your crosshair"))
                .setSaveConsumer(cfg::setChangeTargetColor)
                .build());

        // ---- Colors ----------------------------------------------------------------------
        ConfigCategory colors = builder.getOrCreateCategory(text("Colors"));

        colors.addEntry(eb.startAlphaColorField(text("Box Color"), cfg.boxColor())
                .setDefaultValue(ModConfig.DEF_BOX_COLOR)
                .setTooltip(text("The base hitbox color (ARGB)"))
                .setSaveConsumer(cfg::setBoxColor)
                .build());

        colors.addEntry(eb.startAlphaColorField(text("Eye Color"), cfg.eyeColor())
                .setDefaultValue(ModConfig.DEF_EYE_COLOR)
                .setTooltip(text("The eye-height band color (ARGB)"))
                .setSaveConsumer(cfg::setEyeColor)
                .build());

        colors.addEntry(eb.startAlphaColorField(text("View Color"), cfg.viewColor())
                .setDefaultValue(ModConfig.DEF_VIEW_COLOR)
                .setTooltip(text("The look-direction segment color (ARGB)"))
                .setSaveConsumer(cfg::setViewColor)
                .build());

        colors.addEntry(eb.startAlphaColorField(text("Target Color"), cfg.targetColor())
                .setDefaultValue(ModConfig.DEF_TARGET_COLOR)
                .setTooltip(text("The box color when the entity is targeted (ARGB)"))
                .setSaveConsumer(cfg::setTargetColor)
                .build());

        // ---- Line ------------------------------------------------------------------------
        ConfigCategory line = builder.getOrCreateCategory(text("Line"));

        line.addEntry(eb.startFloatField(text("Line Width"), cfg.lineWidth())
                .setMin(ModConfig.LINE_WIDTH_MIN)
                .setMax(ModConfig.LINE_WIDTH_MAX)
                .setDefaultValue(ModConfig.DEF_LINE_WIDTH)
                .setTooltip(text("Width of the hitbox lines"))
                .setSaveConsumer(cfg::setLineWidth)
                .build());

        line.addEntry(eb.startDoubleField(text("Radius"), cfg.radius())
                .setMin(ModConfig.RADIUS_MIN)
                .setMax(ModConfig.RADIUS_MAX)
                .setDefaultValue(ModConfig.DEF_RADIUS)
                .setTooltip(text("How far (in blocks) to scan for entities to outline"))
                .setSaveConsumer(cfg::setRadius)
                .build());

        return builder.build();
    }

    // Mapping-gated text factory: Yarn Text on fabric && <26.1, Mojmap Component elsewhere.
    //? if fabric {
    //? if >=26.1 {
    /*private static Component text(String s) { return Component.literal(s); }
    *///?} else {
    private static Text text(String s) { return Text.literal(s); }
    //?}
    //?}
    //? if neoforge {
    /*private static Component text(String s) { return Component.literal(s); }*/
    //?}
}
