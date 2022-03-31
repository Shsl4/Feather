package dev.sl4sh.feather.client;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.client.rendering.RenderHandler;
import dev.sl4sh.feather.server.FeatherServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FeatherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RenderHandler.register(Feather.getEventRegistry());
    }
}
