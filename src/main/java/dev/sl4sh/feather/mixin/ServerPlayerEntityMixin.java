package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.player.*;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        Feather.getEventRegistry().POST_DEATH.invoke(event);

    }

    @Inject(at = @At(value = "HEAD"), method = "moveToWorld", cancellable = true)
    public void onPreWorldChange(ServerWorld destination, CallbackInfoReturnable<Entity> info) {

        ServerPlayerEntity player = Utilities.as(this);
        Vec3d pos = player.getPos();
        Vec2f rot = player.getRotationClient();

        PlayerPreDimensionChangeEvent event = new PlayerPreDimensionChangeEvent(destination, player, pos, rot);
        Feather.getEventRegistry().PRE_DIM_CHANGE.invoke(event);

        if(event.isCancelled()){
            info.cancel();
        }

    }

    @Inject(at = @At(value = "HEAD"), method = "requestTeleport", cancellable = true)
    public void preRequestTeleport(double destX, double destY, double destZ, CallbackInfo info){

        ServerPlayerEntity player = Utilities.as(this);
        PlayerPreTeleportEvent event = new PlayerPreTeleportEvent(player, new Vec3d(destX, destY, destZ),
                new Vec2f(player.getRoll(), player.getPitch()));
        Feather.getEventRegistry().PRE_TELEPORT.invoke(event);

        if(event.isCancelled()){
            info.cancel();
        }


    }

    @Inject(at = @At(value = "TAIL"), method = "requestTeleport")
    public void postRequestTeleport(double destX, double destY, double destZ, CallbackInfo info){

        ServerPlayerEntity player = Utilities.as(this);

        PlayerPostTeleportEvent event = new PlayerPostTeleportEvent(player, new Vec3d(destX, destY, destZ),
                new Vec2f(player.getRoll(), player.getPitch()));
        Feather.getEventRegistry().POST_TELEPORT.invoke(event);


    }

    @Inject(at = @At(value = "TAIL"), method = "worldChanged")
    public void onPostWorldChange(ServerWorld origin, CallbackInfo info) {

        ServerPlayerEntity player = Utilities.as(this);
        Vec3d pos = player.getPos();
        Vec2f rot = player.getRotationClient();

        PlayerPostDimensionChangeEvent event = new PlayerPostDimensionChangeEvent(player, pos, rot);
        Feather.getEventRegistry().POST_DIM_CHANGE.invoke(event);

    }

    @Inject(at = @At(value = "HEAD"), method = "teleport", cancellable = true)
    public void onPreTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info){

        PlayerPreTeleportEvent event = new PlayerPreTeleportEvent(Utilities.as(this), new Vec3d(x, y, z), new Vec2f(yaw, pitch));
        Feather.getEventRegistry().PRE_TELEPORT.invoke(event);

        if(event.isCancelled()){
            info.cancel();
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "teleport")
    public void onPostTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info){

        PlayerPostTeleportEvent event = new PlayerPostTeleportEvent(Utilities.as(this), new Vec3d(x, y, z), new Vec2f(yaw, pitch));
        Feather.getEventRegistry().POST_TELEPORT.invoke(event);


    }

}
