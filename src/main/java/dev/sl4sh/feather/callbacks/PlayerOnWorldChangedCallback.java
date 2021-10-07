package dev.sl4sh.feather.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public interface PlayerOnWorldChangedCallback {

    Event<PlayerOnWorldChangedCallback> EVENT = EventFactory.createArrayBacked(PlayerOnWorldChangedCallback.class, (listeners) -> (player, server) -> {
        for (PlayerOnWorldChangedCallback listener : listeners){
            ActionResult result = listener.onWorldChanged(player, server);

            if(result != ActionResult.PASS){
                return result;
            }
        }

        return ActionResult.PASS;

    });

    ActionResult onWorldChanged(ServerPlayerEntity player, ServerWorld world);

}
