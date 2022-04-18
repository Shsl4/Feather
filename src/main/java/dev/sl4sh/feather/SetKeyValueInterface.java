package dev.sl4sh.feather;

import net.minecraft.util.registry.RegistryKey;

public interface SetKeyValueInterface<T> {
    void setKeyValue(RegistryKey<T> key, T value);
}
