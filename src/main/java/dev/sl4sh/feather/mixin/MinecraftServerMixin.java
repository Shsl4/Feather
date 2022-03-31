package dev.sl4sh.feather.mixin;

import com.mojang.serialization.Lifecycle;
import dev.sl4sh.feather.MinecraftServerInterface;
import dev.sl4sh.feather.util.Utilities;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

enum DimType{

    VOID,
    OVERWORLD,
    NETHER,
    END

}

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerInterface {

    @Shadow @Final private Executor workerExecutor;
    @Shadow @Final protected LevelStorage.Session session;
    @Shadow @Final private WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    @Shadow @Final protected DynamicRegistryManager.Impl registryManager;

    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow @Final protected SaveProperties saveProperties;

    @Override
    public ServerWorld createWorld(){

        Biome voidBiome = this.registryManager.get(Registry.BIOME_KEY).get(Identifier.tryParse("minecraft:the_void"));

        MinecraftServer server = Utilities.as(this);
        long seed = 236902368;
        DimensionType type = server.getOverworld().getDimension();

        DynamicRegistryManager registryManager = server.getRegistryManager();

        LevelInfo info = new LevelInfo("world", GameMode.CREATIVE, false, Difficulty.NORMAL, true, new GameRules(), DataPackSettings.SAFE_MODE);

        GeneratorOptions options = new GeneratorOptions(seed, false, false,
                GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registryManager.get(Registry.DIMENSION_TYPE_KEY),
                        DimensionType.createDefaultDimensionOptions(registryManager, seed),
                        GeneratorOptions.createOverworldGenerator(registryManager, seed)));

        LevelProperties props = new LevelProperties(info, options, Lifecycle.stable());
        UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(this.saveProperties, props);
        RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, new Identifier("feather:void"));
        WorldGenerationProgressListener listener = this.worldGenerationProgressListenerFactory.create(11);
        DimensionType type2 = DimensionType.create(OptionalLong.empty(), true, true, false, false, 1.0f, false, true, true,true, false, 0, 256, 256, Identifier.tryParse("false"), Identifier.tryParse("false"), 1.0f);
        StructuresConfig config = new StructuresConfig(Optional.empty(), new HashMap<>());
        ChunkGenerator chunk = new FlatChunkGenerator(new FlatChunkGeneratorConfig(config, this.registryManager.get(Registry.BIOME_KEY)));
        DimensionOptions opt = new DimensionOptions(() -> type2, chunk);
        RegistryKey<DimensionOptions> key2 = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("feather:void"));
        this.saveProperties.getGeneratorOptions().getDimensions().add(key2, opt, Lifecycle.stable());

        ServerWorld world = new ServerWorld(server, this.workerExecutor, this.session, unmodifiableLevelProperties, key, type2, listener, chunk, false, seed, new ArrayList<>(), true);
        this.worlds.put(key, world);
//Registry.register(Registry.)
        return world;

    }

    public void destroyWorld(){

        MinecraftServer server = Utilities.as(this);

    }

}
