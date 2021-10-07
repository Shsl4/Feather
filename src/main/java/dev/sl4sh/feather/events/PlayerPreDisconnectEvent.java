package dev.sl4sh.feather.events;

import dev.sl4sh.feather.listener.FeatherEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 *  <p> Event called before a player has disconnected from the server. </p>
 */
public class PlayerPreDisconnectEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final MinecraftServer server;

    public PlayerPreDisconnectEvent(ServerPlayerEntity player){

        this.player = player;
        this.server = player.server;

    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return server;
    }

}
