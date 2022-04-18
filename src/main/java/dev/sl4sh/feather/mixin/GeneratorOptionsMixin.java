package dev.sl4sh.feather.mixin;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionsMixin {


    @Shadow @Final private Registry<DimensionOptions> options;

    @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
    public void validate(CallbackInfoReturnable<DataResult<GeneratorOptions>> cir){

        if (this.options.size() == 1){
            cir.setReturnValue(DataResult.success(Utilities.as(this), Lifecycle.stable()));
            cir.cancel();
        }

    }

}
