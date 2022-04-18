package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.SetKeyValueInterface;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RegistryEntry.Reference.class)
public abstract class RegistryEntryMixin<T> implements SetKeyValueInterface<T> {

    @Shadow abstract void setKeyAndValue(RegistryKey<T> key, T value);

    @Override
    public void setKeyValue(RegistryKey<T> key, T value) {
        setKeyAndValue(key, value);
    }

}
