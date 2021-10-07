package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.EventManager;
import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.events.PlayerPostTeleportEvent;
import dev.sl4sh.feather.events.PlayerPreTeleportEvent;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow @Mutable public ServerPlayerEntity player;

    @Inject(at = @At(value = "HEAD"), method = "requestTeleport(DDDFFLjava/util/Set;Z)V", cancellable = true)
    public void preRequestTeleport(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo info) {

        PlayerPreTeleportEvent event = new PlayerPreTeleportEvent(this.player, new Vec3d(x, y, z), new Vec2f(yaw, pitch));
        EventManager.getOrCreateEvent(PlayerPreTeleportEvent.class).invoker().execute(event);

        if(event.isCancelled()){
            info.cancel();
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "requestTeleport(DDDFFLjava/util/Set;Z)V")
    public void postRequestTeleport(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo info) {

        PlayerPostTeleportEvent event = new PlayerPostTeleportEvent(this.player, new Vec3d(x, y, z), new Vec2f(yaw, pitch));
        EventManager.getOrCreateEvent(PlayerPostTeleportEvent.class).invoker().execute(event);

    }


}
