package dev.sl4sh.feather.mixin;

import com.mojang.brigadier.CommandDispatcher;
import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.commands.FeatherCommandDispatcher;
import dev.sl4sh.feather.event.CommandExecutionEvent;
import dev.sl4sh.feather.event.CommandRegistrationEvent;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {

    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher = new FeatherCommandDispatcher();

    @Shadow public abstract CommandDispatcher<ServerCommandSource> getDispatcher();

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), method = "<init>")
    private void onRegisterCommand(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {

        CommandRegistrationEvent event = new CommandRegistrationEvent((FeatherCommandDispatcher)dispatcher, environment);
        Feather.getEventRegistry().COMMAND_REGISTRATION.invoke(event);

    }

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void execute(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> info) {

        CommandExecutionEvent event = new CommandExecutionEvent(command, commandSource);
        Feather.getEventRegistry().COMMAND_EXECUTION.invoke(event);

        if(event.isCancelled()){

            info.setReturnValue(1);
            commandSource.sendError(new LiteralText("\u00a7c" + event.getCancelReason()));
            info.cancel();

        }

    }

}
