package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.EventManager;
import dev.sl4sh.feather.events.*;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(at = @At(value = "TAIL"), method = "onDeath")
    public void onDeath(DamageSource source, CallbackInfo info) {

        PlayerPostDeathEvent event = new PlayerPostDeathEvent(Utilities.as(this), source);
        EventManager.getOrCreateEvent(PlayerPostDeathEvent.class).invoker().execute(event);

    }

    @Inject(at = @At(value = "HEAD"), method = "moveToWorld", cancellable = true)
    public void onPreWorldChange(ServerWorld destination, CallbackInfoReturnable<Entity> info) {

        ServerPlayerEntity player = Utilities.as(this);
        Vec3d pos = player.getPos();
        Vec2f rot = player.getRotationClient();

        PlayerPreDimensionChangeEvent event = new PlayerPreDimensionChangeEvent(destination, player, pos, rot);
        EventManager.getOrCreateEvent(PlayerPreDimensionChangeEvent.class).invoker().execute(event);

        if(event.isCancelled()){
            info.cancel();
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "worldChanged")
    public void onPostWorldChange(ServerWorld origin, CallbackInfo info) {

        ServerPlayerEntity player = Utilities.as(this);
        Vec3d pos = player.getPos();
        Vec2f rot = player.getRotationClient();

        PlayerPostDimensionChangeEvent event = new PlayerPostDimensionChangeEvent(player, pos, rot);
        EventManager.getOrCreateEvent(PlayerPostDimensionChangeEvent.class).invoker().execute(event);

    }

    @Inject(at = @At(value = "HEAD"), method = "teleport", cancellable = true)
    public void onPreTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info){

        PlayerPreTeleportEvent event = new PlayerPreTeleportEvent(Utilities.as(this), new Vec3d(x, y, z), new Vec2f(yaw, pitch));
        EventManager.getOrCreateEvent(PlayerPreTeleportEvent.class).invoker().execute(event);

        if(event.isCancelled()){
            info.cancel();
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "teleport")
    public void onPostTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info){

        PlayerPostTeleportEvent event = new PlayerPostTeleportEvent(Utilities.as(this), new Vec3d(x, y, z), new Vec2f(yaw, pitch));
        EventManager.getOrCreateEvent(PlayerPostTeleportEvent.class).invoker().execute(event);

    }

}
