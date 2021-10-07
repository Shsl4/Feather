package dev.sl4sh.feather.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerDisconnectCallback {

    Event<PlayerDisconnectCallback> EVENT = EventFactory.createArrayBacked(PlayerDisconnectCallback.class, (listeners) -> (player, server) -> {
        for (PlayerDisconnectCallback listener : listeners){
            ActionResult result = listener.onPlayerDisconnect(player, server);

            if(result != ActionResult.PASS){
                return result;
            }
        }

        return ActionResult.PASS;

    });

    ActionResult onPlayerDisconnect(ServerPlayerEntity player, MinecraftServer server);

}
