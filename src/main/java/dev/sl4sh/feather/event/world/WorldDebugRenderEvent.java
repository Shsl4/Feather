package dev.sl4sh.feather.event.world;

import dev.sl4sh.feather.event.FeatherEvent;
import net.fabricmc.fabric.impl.client.rendering.WorldRenderContextImpl;

public class WorldDebugRenderEvent implements FeatherEvent{

    private final WorldRenderContextImpl context;

    public WorldDebugRenderEvent(WorldRenderContextImpl context) {
        this.context = context;
    }

    public WorldRenderContextImpl getContext() {
        return context;
    }
}
