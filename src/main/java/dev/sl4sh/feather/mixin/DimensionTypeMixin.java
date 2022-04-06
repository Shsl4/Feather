package dev.sl4sh.feather.mixin;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {

    @Inject(at = @At(value = "HEAD"), method = "getSaveDirectory", cancellable = true)
    private static void getSaveDirectory(RegistryKey<World> worldRef, Path worldDirectory, CallbackInfoReturnable<Path> info) {
        if (worldRef == World.OVERWORLD) {
            info.setReturnValue(worldDirectory);
        }
        else if (worldRef == World.END) {
            info.setReturnValue(worldDirectory.resolve("DIM1"));
        }
        else if (worldRef == World.NETHER) {
            info.setReturnValue(worldDirectory.resolve("DIM-1"));
        }
        else info.setReturnValue(worldDirectory.resolve(worldRef.getValue().getPath()));
        info.cancel();
    }

}
