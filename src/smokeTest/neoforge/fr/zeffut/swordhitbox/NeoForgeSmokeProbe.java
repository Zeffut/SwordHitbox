package fr.zeffut.swordhitbox;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

@Mod(value = "swordhitbox_smoke", dist = Dist.CLIENT)
public final class NeoForgeSmokeProbe {
    public NeoForgeSmokeProbe() {
        SmokeProbe probe = new SmokeProbe();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) ->
                probe.tick(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener((SubmitCustomGeometryEvent event) -> probe.render());
    }
}
