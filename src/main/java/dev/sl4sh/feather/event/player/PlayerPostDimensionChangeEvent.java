package dev.sl4sh.feather.event.player;

import dev.sl4sh.feather.event.FeatherEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PlayerPostDimensionChangeEvent implements FeatherEvent {

    private final ServerPlayerEntity player;
    private final Vec3d position;
    private final Vec2f rotation;
    private final ServerWorld origin;

    public PlayerPostDimensionChangeEvent(ServerPlayerEntity player, Vec3d position, Vec2f rotation, ServerWorld origin){

        this.player = player;
        this.position = position;
        this.rotation = rotation;
        this.origin = origin;

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

    public ServerWorld getOrigin() { return origin; }
}
