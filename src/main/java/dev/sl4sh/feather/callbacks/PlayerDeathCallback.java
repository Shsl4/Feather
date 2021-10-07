package dev.sl4sh.feather.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerDeathCallback {

    Event<PlayerDeathCallback> EVENT = EventFactory.createArrayBacked(PlayerDeathCallback.class, (listeners) -> (player, server) -> {
        for (PlayerDeathCallback listener : listeners){
            ActionResult result = listener.onDeath(player, server);
            if(result != ActionResult.PASS){
                return result;
            }
        }

        return ActionResult.PASS;

    });

    ActionResult onDeath(ServerPlayerEntity player, DamageSource source);

}
