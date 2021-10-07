package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.callbacks.PlayerConnectedCallback;
import dev.sl4sh.feather.callbacks.PlayerConnectingCallback;
import dev.sl4sh.feather.callbacks.PlayerDisconnectCallback;
import dev.sl4sh.feather.callbacks.PlayerRespawnCallback;
import dev.sl4sh.feather.events.PlayerConnectedEvent;
import dev.sl4sh.feather.events.PlayerConnectingEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at = @At(value = "HEAD"), method = "onPlayerConnect", cancellable = true)
    private void onPlayerConnecting(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {

        PlayerConnectingEvent event = new PlayerConnectingEvent(connection, player);
        PlayerConnectingCallback.EVENT.invoker().onPlayerConnect(event);

        if (event.isCancelled()){
            info.cancel();
            connection.disconnect(new LiteralText(event.getCancelReason()));
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerConnected(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {

        PlayerConnectedEvent event = new PlayerConnectedEvent(connection, player);
        PlayerConnectedCallback.EVENT.invoker().onPlayerConnect(event);

    }

    @Inject(at = @At(value = "TAIL"), method = "remove", cancellable = true)
    private void remove(ServerPlayerEntity player, CallbackInfo info) {

        ActionResult result = PlayerDisconnectCallback.EVENT.invoker().onPlayerDisconnect(player, player.server);

        if (result == ActionResult.FAIL) {
            info.cancel();
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "respawnPlayer", cancellable = true)
    private void respawnPlayer(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info){
        ActionResult result = PlayerRespawnCallback.EVENT.invoker().onRespawn(player, alive);
        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
