package dev.sl4sh.feather.events;

import dev.sl4sh.feather.listener.FeatherEvent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 *  <p> Event called after a player has connected to the server. </p>
 */
public class PlayerPostDeathEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final DamageSource source;

    public PlayerPostDeathEvent(ServerPlayerEntity player, DamageSource source){

        this.player = player;

        this.source = source;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return player.getServer();
    }

    public DamageSource getDamageSource() {
        return source;
    }
}
