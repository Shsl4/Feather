package dev.sl4sh.feather.event.player;

import dev.sl4sh.feather.event.FeatherEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 *  <p> Event called after a player has connected to the server. </p>
 */
public class PlayerPostConnectEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final ClientConnection connection;

    public PlayerPostConnectEvent(ClientConnection connection, ServerPlayerEntity player){

        this.player = player;
        this.connection = connection;

    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return player.getServer();
    }

    public ClientConnection getConnection() {
        return connection;
    }

}
