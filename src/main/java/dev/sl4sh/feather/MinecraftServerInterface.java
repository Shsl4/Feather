package dev.sl4sh.feather;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;

import java.io.IOException;
import java.util.Optional;

public interface MinecraftServerInterface {

    public Optional<ServerWorld> createWorld();
    public boolean loadWorld(String name);
    public boolean deleteWorld(String name) throws IOException;

}
