package dev.sl4sh.feather.events;

import dev.sl4sh.feather.listener.CancellableEvent;
import dev.sl4sh.feather.listener.FeatherEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PlayerPostTeleportEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final Vec3d position;
    private final Vec2f rotation;

    public PlayerPostTeleportEvent(ServerPlayerEntity player, Vec3d position, Vec2f rotation){

        this.player = player;
        this.position = position;
        this.rotation = rotation;

    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public MinecraftServer getServer() {
        return player.getServer();
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec2f getRotation() {
        return rotation;
    }
}
