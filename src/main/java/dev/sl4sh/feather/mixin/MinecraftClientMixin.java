package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.client.ClientDisconnectEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void onClientDisconnect(Screen screen, CallbackInfo info){

        ClientDisconnectEvent event = new ClientDisconnectEvent(screen);
        Feather.getEventRegistry().CLIENT_SIDE_DISCONNECT.invoke(event);

    }

}
