package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.callbacks.PlayerDeathCallback;
import dev.sl4sh.feather.callbacks.PlayerOnWorldChangedCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "onDeath", cancellable = true)
    public void onDeath(DamageSource source, CallbackInfo info) {

        ActionResult result = PlayerDeathCallback.EVENT.invoker().onDeath((ServerPlayerEntity) (Object) this, source);

        if (result == ActionResult.FAIL) {
            info.cancel();
        }

    }

    @Inject(at = @At(value = "HEAD"), method = "worldChanged", cancellable = true)
    public void onWorldChanged(ServerWorld origin, CallbackInfo info) {

        ActionResult result = PlayerOnWorldChangedCallback.EVENT.invoker().onWorldChanged((ServerPlayerEntity) (Object) this, origin);

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
