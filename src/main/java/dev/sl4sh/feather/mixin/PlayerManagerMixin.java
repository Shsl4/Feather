package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.player.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at = @At(value = "HEAD"), method = "onPlayerConnect", cancellable = true)
    private void onPlayerConnect_Pre(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {

        PlayerPreConnectEvent event = new PlayerPreConnectEvent(connection, player);
        Feather.getEventRegistry().PRE_CONNECT.invoke(event);

        if (event.isCancelled()){
            info.cancel();
            connection.disconnect(new LiteralText(event.getCancelReason()));
        }

    }

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerConnect_Post(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {

        PlayerPostConnectEvent event = new PlayerPostConnectEvent(connection, player);
        Feather.getEventRegistry().POST_CONNECT.invoke(event);

    }

    @Inject(at = @At(value = "HEAD"), method = "remove")
    private void onPlayerPreDisconnect_Pre(ServerPlayerEntity player, CallbackInfo info) {

        PlayerPreDisconnectEvent event = new PlayerPreDisconnectEvent(player);
        Feather.getEventRegistry().PRE_DISCONNECT.invoke(event);

    }

    @Inject(at = @At(value = "TAIL"), method = "remove")
    private void onPlayerDisconnect_Post(ServerPlayerEntity player, CallbackInfo info) {

        PlayerPostDisconnectEvent event = new PlayerPostDisconnectEvent(player);
        Feather.getEventRegistry().POST_DISCONNECT.invoke(event);

    }

    @Inject(at = @At(value = "HEAD"), method = "respawnPlayer")
    private void respawnPlayer_Pre(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info){

        PlayerPreRespawnEvent event = new PlayerPreRespawnEvent(player, alive);
        Feather.getEventRegistry().PRE_RESPAWN.invoke(event);

    }

    @Inject(at = @At(value = "TAIL"), method = "respawnPlayer")
    private void respawnPlayer_Post(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info){

        PlayerPostRespawnEvent event = new PlayerPostRespawnEvent(info.getReturnValue());
        Feather.getEventRegistry().POST_RESPAWN.invoke(event);

    }

}
