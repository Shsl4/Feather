package dev.sl4sh.feather.events;

import dev.sl4sh.feather.listener.FeatherCancellableEvent;
import dev.sl4sh.feather.listener.FeatherEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 *  <p> Event called AFTER a player has connected to the server. </p>
 */
public class PlayerConnectedEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final MinecraftServer server;
    private final ClientConnection connection;

    public PlayerConnectedEvent(ClientConnection connection, ServerPlayerEntity player){

        this.player = player;
        this.server = player.server;
        this.connection = connection;

    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ClientConnection getConnection() {
        return connection;
    }

}
