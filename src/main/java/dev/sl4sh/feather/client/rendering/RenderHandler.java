package dev.sl4sh.feather.client.rendering;

import dev.sl4sh.feather.client.rendering.linerenderer.LineRenderer;
import dev.sl4sh.feather.event.client.ClientDisconnectEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.event.registration.EventResponder;
import dev.sl4sh.feather.event.registration.Register;
import dev.sl4sh.feather.event.world.WorldDebugRenderEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.WorldRenderContextImpl;
import net.minecraft.util.math.Vec3d;

@EventResponder
@Environment(EnvType.CLIENT)
public class RenderHandler{

    @Register
    public static void register(EventRegistry registry){
        registry.DEBUG_RENDER.register(RenderHandler::onRender);
        registry.CLIENT_SIDE_DISCONNECT.register(RenderHandler::onDisconnect);
    }

    private static void onRender(WorldDebugRenderEvent event){

        WorldRenderContextImpl context = event.getContext();
        Vec3d pos = context.camera().getPos();

        if(context.consumers() != null) {

            LineRenderer.INSTANCE.render(context.matrixStack(), context.consumers(), pos.x, pos.y, pos.z);

        }


    }

    private static void onDisconnect(ClientDisconnectEvent event){

        LineRenderer.INSTANCE.clearLines();

    }

}
