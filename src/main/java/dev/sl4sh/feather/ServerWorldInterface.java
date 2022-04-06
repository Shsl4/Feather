package dev.sl4sh.feather;

import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;

public interface ServerWorldInterface {
    void setRegistryManager(DynamicRegistryManager manager);
}
