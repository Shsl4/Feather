package dev.sl4sh.feather;

import dev.sl4sh.feather.util.Utilities;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MutImpl
        implements DynamicRegistryManager.Mutable {
    private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> mutableRegistries;

    public MutImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> mutableRegistries) {
        this.mutableRegistries = mutableRegistries;
    }

    @Override
    public <E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> key) {
        return Optional.ofNullable(this.mutableRegistries.get(key)).map(Utilities::as);
    }

    @Override
    public <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> key) {
        return Optional.ofNullable(this.mutableRegistries.get(key)).map(Utilities::as);
    }

    @Override
    public Stream<Entry<?>> streamManagedRegistries() {
        return this.mutableRegistries.entrySet().stream().map((a) -> new Entry<>(Utilities.as(a.getKey()), Utilities.as(a.getValue())));
    }

}