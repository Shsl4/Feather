package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.ServerWorldInterface;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldInterface {

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Nullable
    private DynamicRegistryManager customManager;

    @Inject(method = "getRegistryManager", at = @At("HEAD"), cancellable = true)
    void getRegistryManager(CallbackInfoReturnable<DynamicRegistryManager> info){

        if(customManager == null){
            info.setReturnValue(this.getServer().getRegistryManager());
        }
        else{
            info.setReturnValue(customManager);
        }


        info.cancel();
    }


    @Override
    public void setRegistryManager(DynamicRegistryManager manager) {
        this.customManager = manager;
    }
}
