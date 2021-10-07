package dev.sl4sh.feather.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerRespawnCallback {

    Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class, (listeners) -> (player, alive) -> {
        for (PlayerRespawnCallback listener : listeners){
            ActionResult result = listener.onRespawn(player, alive);

            if(result != ActionResult.PASS){
                return result;
            }
        }

        return ActionResult.PASS;

    });

    ActionResult onRespawn(ServerPlayerEntity player, boolean alive);

}
