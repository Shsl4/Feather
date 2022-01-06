package dev.sl4sh.feather.event.player;

import dev.sl4sh.feather.event.FeatherEvent;
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

    public ServerWorld getWorld() { return player.getWorld(); }


}
