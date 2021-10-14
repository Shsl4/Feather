package dev.sl4sh.feather.event.player;

import dev.sl4sh.feather.event.CancellableEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerPreConnectEvent extends CancellableEvent {

    private final ServerPlayerEntity player;
    private final ClientConnection connection;

    public PlayerPreConnectEvent(ClientConnection connection, ServerPlayerEntity player){

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
