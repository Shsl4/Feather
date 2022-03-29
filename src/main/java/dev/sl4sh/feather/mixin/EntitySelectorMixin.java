package dev.sl4sh.feather.mixin;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.sl4sh.feather.Feather;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin {

    @Shadow @Final private boolean usesAt;

    @Inject(method = "checkSourcePermission", at = @At(value = "HEAD"), cancellable = true)
    private void checkSourcePermission(ServerCommandSource source, CallbackInfo info) throws CommandSyntaxException {

        ServerPlayerEntity player = source.getPlayer();

        if(this.usesAt && !Feather.getPermissionService().hasPermission("*", player)){
            Message message = Text.of("Only admins can use selectors.");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        info.cancel();

    }

}
