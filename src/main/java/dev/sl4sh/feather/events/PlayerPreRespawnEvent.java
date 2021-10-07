package dev.sl4sh.feather.events;

import dev.sl4sh.feather.listener.CancellableEvent;
import dev.sl4sh.feather.listener.FeatherEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 *  <p> Event called before a player has respawned. </p>
 */
public class PlayerPreRespawnEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final boolean alive;

    public PlayerPreRespawnEvent(ServerPlayerEntity player, boolean alive){

        this.player = player;
        this.alive = alive;

    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return player.getServer();
    }

    public ServerWorld getWorld() { return player.getServerWorld(); }

    public boolean isAlive() { return this.alive; }

}
