package fr.zeffut.swordhitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public final class FabricSmokeProbe implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SmokeProbe probe = new SmokeProbe();
        ClientTickEvents.END_CLIENT_TICK.register(probe::tick);
        LevelRenderEvents.COLLECT_SUBMITS.register(ctx -> probe.render());
    }
}
