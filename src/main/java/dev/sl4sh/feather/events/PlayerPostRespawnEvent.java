package dev.sl4sh.feather.events;

import dev.sl4sh.feather.listener.CancellableEvent;
import dev.sl4sh.feather.listener.FeatherEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 *  <p> Event called after a player has respawned. </p>
 */
public class PlayerPostRespawnEvent implements FeatherEvent {

    private final ServerPlayerEntity player;

    public PlayerPostRespawnEvent(ServerPlayerEntity player){

        this.player = player;

    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return player.getServer();
    }

    public ServerWorld getWorld() { return player.getServerWorld(); }


}
