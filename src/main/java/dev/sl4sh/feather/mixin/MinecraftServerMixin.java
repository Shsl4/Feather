package dev.sl4sh.feather.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import dev.sl4sh.feather.*;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resource.*;
import net.minecraft.server.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

enum DimType{

    VOID,
    OVERWORLD,
    NETHER,
    END

}

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerInterface {

    @Shadow @Final private Executor workerExecutor;
    @Shadow @Final protected LevelStorage.Session session;
    @Shadow @Final private WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    @Shadow @Final private DynamicRegistryManager.Immutable registryManager;

    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow @Final protected SaveProperties saveProperties;

    @Shadow private boolean preventProxyConnections;

    @Shadow private PlayerManager playerManager;

    @Shadow public abstract DynamicRegistryManager.Immutable getRegistryManager();

    @Override
    public Optional<ServerWorld> createWorld(){

        return Optional.empty();

        /*

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
        RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, new Identifier("void"));
        WorldGenerationProgressListener listener = this.worldGenerationProgressListenerFactory.create(11);
        DimensionType type2 = DimensionType.create(OptionalLong.empty(), true, true, false, false, 1.0f, false, true, true,true, false, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, new Identifier("void"), 1.0f);
        //StructuresConfig config = new StructuresConfig(Optional.empty(), new HashMap<>());
        RegistryKey<DimensionType> key2 = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("void"));
        SimpleRegistry<DimensionType> reg = new SimpleRegistry<>(Registry.DIMENSION_TYPE_KEY, Lifecycle.stable(), null);
        RegistryEntry<DimensionType> entry = reg.add(key2, type2, Lifecycle.stable());
        ChunkGenerator chunk = new FlatChunkGenerator(registryManager.get(Registry.STRUCTURE_SET_KEY), new FlatChunkGeneratorConfig(Optional.empty(), this.registryManager.get(Registry.BIOME_KEY)));
        DimensionOptions opt = new DimensionOptions(entry, chunk);
        //File file = session.getWorldDirectory(key).resolve("data").toFile();

        ServerWorld world = new ServerWorld(server, this.workerExecutor, this.session, unmodifiableLevelProperties, key, entry, listener, chunk, false, seed, new ArrayList<>(), true);
        this.worlds.put(key, world);
        GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
        return world;*/

    }

    private SaveLoader.SavePropertiesSupplier loadFromWorld(LevelStorage.Session session) {
        return (resourceManager, dataPackSettings) -> {

            DynamicRegistryManager manager = getRegistryManager();

            FeatherRegistry<DimensionType> dimensionRegistry = new FeatherRegistry<>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental());
            FeatherRegistry<DimensionOptions> optionsRegistry = new FeatherRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental());
            FeatherRegistry<World> worldRegistry = new FeatherRegistry<>(Registry.WORLD_KEY, Lifecycle.experimental());

            FeatherRegistry<Biome> biomeRegistry = FeatherRegistry.from(manager, Registry.BIOME_KEY);
            FeatherRegistry<StructureSet> structureRegistry = new FeatherRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable());
            FeatherRegistry<PlacedFeature> placedFeatureRegistry = new FeatherRegistry<>(Registry.PLACED_FEATURE_KEY, Lifecycle.stable());
            FeatherRegistry<ConfiguredFeature<?, ?>> confFeatureRegistry = new FeatherRegistry<>(Registry.CONFIGURED_FEATURE_KEY, Lifecycle.stable());
            FeatherRegistry<ConfiguredCarver<?>> confCarverRegistry = new FeatherRegistry<>(Registry.CONFIGURED_CARVER_KEY, Lifecycle.stable());

            Map<RegistryKey<? extends Registry<?>>, MutableRegistry<?>> map = new HashMap<>();

            map.put(Registry.PLACED_FEATURE_KEY, placedFeatureRegistry);
            map.put(Registry.CONFIGURED_CARVER_KEY, confCarverRegistry);
            map.put(Registry.CONFIGURED_FEATURE_KEY, confFeatureRegistry);
            map.put(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, FeatherRegistry.from(manager, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY));
            map.put(Registry.STRUCTURE_PROCESSOR_LIST_KEY, FeatherRegistry.from(manager, Registry.STRUCTURE_PROCESSOR_LIST_KEY));
            map.put(Registry.STRUCTURE_POOL_KEY, FeatherRegistry.from(manager, Registry.STRUCTURE_POOL_KEY));
            map.put(Registry.CHUNK_GENERATOR_SETTINGS_KEY, FeatherRegistry.from(manager, Registry.CHUNK_GENERATOR_SETTINGS_KEY));
            map.put(Registry.DENSITY_FUNCTION_KEY, FeatherRegistry.from(manager, Registry.DENSITY_FUNCTION_KEY));
            map.put(Registry.NOISE_WORLDGEN, FeatherRegistry.from(manager, Registry.NOISE_WORLDGEN));

            map.put(Registry.WORLD_KEY, worldRegistry);
            map.put(Registry.DIMENSION_TYPE_KEY, dimensionRegistry);
            map.put(Registry.DIMENSION_KEY, optionsRegistry);
            map.put(Registry.BIOME_KEY, biomeRegistry);
            map.put(Registry.STRUCTURE_SET_KEY, structureRegistry);

            dimensionRegistry.add(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("void")), null, Lifecycle.stable());

            DynamicRegistryManager.Mutable regManager = new MutImpl(map);
            RegistryOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, regManager, resourceManager);
            SaveProperties saveProperties = session.readLevelProperties(dynamicOps, dataPackSettings, regManager.getRegistryLifecycle());
            if (saveProperties == null) {
                throw new IllegalStateException("Failed to load world");
            }
            return Pair.of(saveProperties, new DynamicRegistryManager.ImmutableImpl(map));
        };
    }

    @Override
    public boolean loadWorld(String name) {

        try{

            MinecraftServer server = Utilities.as(this);
            LevelStorage storage = new LevelStorage(Path.of("world"), Path.of("world", name, "backups"), Schemas.getFixer());
            LevelStorage.Session session = storage.createSession(name);
            
            File dataPackPath = session.getDirectory(WorldSavePath.DATAPACKS).toFile();
            ResourcePackProvider packProvider = new FileResourcePackProvider(dataPackPath, ResourcePackSource.PACK_SOURCE_WORLD);
            ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), packProvider);

            SaveLoader.FunctionLoaderConfig functionLoaderConfig = new SaveLoader.FunctionLoaderConfig(resourcePackManager,
                    CommandManager.RegistrationEnvironment.DEDICATED, 5, false);

            SaveLoader.DataPackSettingsSupplier packSupplier = SaveLoader.DataPackSettingsSupplier.loadFromWorld(session);
            SaveLoader.SavePropertiesSupplier saveSupplier = loadFromWorld(session);

            SaveLoader loader = SaveLoader.ofLoaded(functionLoaderConfig, packSupplier, saveSupplier, Util.getMainWorkerExecutor(), Runnable::run).get();
            SaveProperties properties = loader.saveProperties();

            loader.refresh();

            DynamicRegistryManager registryManager = loader.dynamicRegistryManager();
            Registry<DimensionOptions> dimensionRegistry = registryManager.get(Registry.DIMENSION_KEY);

            WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
            GeneratorOptions options = properties.getGeneratorOptions();
            long seed = BiomeAccess.hashSeed(options.getSeed());

            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : dimensionRegistry.getEntrySet()) {

                RegistryKey<DimensionOptions> dimensionKey = entry.getKey();
                RegistryKey<World> worldKey = RegistryKey.of(Registry.WORLD_KEY, dimensionKey.getValue());
                RegistryEntry<DimensionType> dimensionType = entry.getValue().getDimensionTypeSupplier();
                ChunkGenerator chunkGenerator = entry.getValue().getChunkGenerator();
                ServerWorldProperties serverWorldProperties = properties.getMainWorldProperties();

                UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(properties, serverWorldProperties);

                ServerWorld world = new ServerWorld(server, this.workerExecutor, session, unmodifiableLevelProperties,
                        worldKey, dimensionType, worldGenerationProgressListener, chunkGenerator, false,
                        seed, ImmutableList.of(), true);

                this.worlds.put(worldKey, world);

                WorldBorder border = world.getWorldBorder();
                border.load(serverWorldProperties.getWorldBorder());

                session.backupLevelDataFile(registryManager, saveProperties, this.playerManager.getUserData());
                session.save(name);

            }

        }
        catch(Exception e){

            e.printStackTrace();
            Feather.getLogger().info("Failed to load world {}!", name);
            return false;

        }

        return true;

    }

    @Override
    public boolean deleteWorld(String name) throws IOException {

        try{

            MinecraftServer server = Utilities.as(this);
            String worldName = "void";
            GameMode gameMode = GameMode.SURVIVAL;
            Difficulty difficulty = Difficulty.NORMAL;
            long seed = 0L;
            SaveLoader saveLoader;
            boolean bonusChest = false;
            DynamicRegistryManager manager = getRegistryManager();

            LevelStorage storage = new LevelStorage(Path.of("world"), Path.of("world", worldName, "backups"), Schemas.getFixer());
            LevelStorage.Session session = storage.createSession(worldName);
            DimensionType type2 = DimensionType.create(OptionalLong.empty(), true, true, true, true, 1.0f, false, true, true,true, false, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, new Identifier("void"), 1.0f);

            FeatherRegistry<DimensionType> dimensionRegistry = new FeatherRegistry<>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental());
            FeatherRegistry<DimensionOptions> optionsRegistry = new FeatherRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental());
            FeatherRegistry<World> worldRegistry = new FeatherRegistry<>(Registry.WORLD_KEY, Lifecycle.experimental());

            FeatherRegistry<Biome> biomeRegistry = FeatherRegistry.from(manager, Registry.BIOME_KEY);
            FeatherRegistry<StructureSet> structureRegistry = new FeatherRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable());
            FeatherRegistry<PlacedFeature> placedFeatureRegistry = new FeatherRegistry<>(Registry.PLACED_FEATURE_KEY, Lifecycle.stable());
            FeatherRegistry<ConfiguredFeature<?, ?>> confFeatureRegistry = new FeatherRegistry<>(Registry.CONFIGURED_FEATURE_KEY, Lifecycle.stable());
            FeatherRegistry<ConfiguredCarver<?>> confCarverRegistry = new FeatherRegistry<>(Registry.CONFIGURED_CARVER_KEY, Lifecycle.stable());

            Map<RegistryKey<? extends Registry<?>>, Registry<?>> map = new HashMap<>();

            map.put(Registry.PLACED_FEATURE_KEY, placedFeatureRegistry);
            map.put(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, FeatherRegistry.from(manager, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY));
            map.put(Registry.STRUCTURE_FEATURE_KEY, confFeatureRegistry);
            map.put(Registry.CONFIGURED_CARVER_KEY, confCarverRegistry);
            map.put(Registry.NOISE_WORLDGEN, FeatherRegistry.from(manager, Registry.NOISE_WORLDGEN));

            map.put(Registry.WORLD_KEY, worldRegistry);
            map.put(Registry.DIMENSION_TYPE_KEY, dimensionRegistry);
            map.put(Registry.DIMENSION_KEY, optionsRegistry);
            map.put(Registry.BIOME_KEY, biomeRegistry);
            map.put(Registry.STRUCTURE_SET_KEY, structureRegistry);

            DynamicRegistryManager regManager = new DynamicRegistryManager.ImmutableImpl(map);

            Registry<ConfiguredCarver<?>> regg = this.registryManager.get(Registry.CONFIGURED_CARVER_KEY);
            List<RegistryEntry.Reference<ConfiguredCarver<?>>> list3 = regg.streamEntries().toList();
            for (RegistryEntry.Reference<ConfiguredCarver<?>> ref : list3){
                confCarverRegistry.add(RegistryKey.of(confCarverRegistry.getKey(), ref.getKey().get().getValue()), ref.value(), Lifecycle.stable());
            }

            Registry<ConfiguredFeature<?, ?>> confFeatures = this.registryManager.get(Registry.CONFIGURED_FEATURE_KEY);
            List<RegistryEntry.Reference<ConfiguredFeature<?, ?>>> list2 = confFeatures.streamEntries().toList();
            for (RegistryEntry.Reference<ConfiguredFeature<?, ?>> ref : list2){
                confFeatureRegistry.add(RegistryKey.of(confFeatureRegistry.getKey(), ref.getKey().get().getValue()), ref.value(), Lifecycle.stable());
            }

            Registry<PlacedFeature> placedFeatures = this.registryManager.get(Registry.PLACED_FEATURE_KEY);
            List<RegistryEntry.Reference<PlacedFeature>> list = placedFeatures.streamEntries().toList();
            for (RegistryEntry.Reference<PlacedFeature> feature : list){
                placedFeatureRegistry.add(RegistryKey.of(placedFeatureRegistry.getKey(), feature.getKey().get().getValue()),
                        new PlacedFeature(confFeatureRegistry.getEntry(feature.value().feature().getKey().get()).get(),
                                feature.value().placementModifiers()),
                        Lifecycle.stable());
            }


            PlacedFeature f = placedFeatureRegistry.get(new Identifier("minecraft:freeze_top_layer"));

            /*
            Biome voidBiome = biomeRegistry.get(Identifier.tryParse("minecraft:the_void"));

            biomeRegistry.add(BiomeKeys.PLAINS, voidBiome, Lifecycle.stable());*/

            FlatChunkGenerator chunkGenerator = new FlatChunkGenerator(structureRegistry, new FlatChunkGeneratorConfig(Optional.empty(), biomeRegistry));

            optionsRegistry.add(DimensionOptions.OVERWORLD, new DimensionOptions(null, chunkGenerator), Lifecycle.stable());

            GeneratorOptions options = new GeneratorOptions(seed, false, bonusChest, optionsRegistry);
            optionsRegistry.removeEntry(DimensionOptions.OVERWORLD);

            RegistryEntry<DimensionType> typeEntry = dimensionRegistry.add(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("void")), type2, Lifecycle.stable());
            DimensionOptions dimOptions = new DimensionOptions(typeEntry, chunkGenerator);
            optionsRegistry.add(RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("void")), dimOptions, Lifecycle.stable());

            ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
            try {
                SaveLoader.FunctionLoaderConfig functionLoaderConfig = new SaveLoader.FunctionLoaderConfig(resourcePackManager, CommandManager.RegistrationEnvironment.DEDICATED, 5, false);
                saveLoader = SaveLoader.ofLoaded(functionLoaderConfig, () -> {
                    DataPackSettings dataPackSettings = session.getDataPackSettings();
                    return dataPackSettings == null ? DataPackSettings.SAFE_MODE : dataPackSettings;
                }, (resourceManager, dataPackSettings) -> {
                    GeneratorOptions generatorOptions;
                    LevelInfo levelInfo;
                    DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
                    RegistryOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, mutable, resourceManager);
                    SaveProperties saveProperties = session.readLevelProperties(dynamicOps, dataPackSettings, mutable.getRegistryLifecycle());
                    if (saveProperties != null) {
                        return Pair.of(saveProperties, mutable.toImmutable());
                    }

                    levelInfo = new LevelInfo(worldName, gameMode, false, difficulty, true, new GameRules(), dataPackSettings);
                    generatorOptions = bonusChest ? options.withBonusChest() : options;

                    LevelProperties levelProperties = new LevelProperties(levelInfo, generatorOptions, Lifecycle.stable());
                    return Pair.of(levelProperties, mutable.toImmutable());

                }, Util.getMainWorkerExecutor(), Runnable::run).get();
            }
            catch (Exception exception) {
                Feather.getLogger().warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", exception);
                resourcePackManager.close();
                return false;
            }

            saveLoader.refresh();
            SaveProperties saveProperties = saveLoader.saveProperties();
            WorldGenerationProgressListener listener = this.worldGenerationProgressListenerFactory.create(11);

            ServerWorldProperties props = saveProperties.getMainWorldProperties();

            RegistryKey<DimensionType> key2 = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("void"));
            SimpleRegistry<DimensionType> reg = new SimpleRegistry<>(Registry.DIMENSION_TYPE_KEY, Lifecycle.stable(), null);
            RegistryEntry<DimensionType> entry = reg.add(key2, type2, Lifecycle.stable());
            RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, new Identifier("void"));

            ServerWorld world = new ServerWorld(server, this.workerExecutor, session, props, key, entry, listener, chunkGenerator, false, seed, new ArrayList<>(), true);

            ServerWorldInterface worldInterface = Utilities.as(world);
            worldInterface.setRegistryManager(regManager);
            this.worlds.put(key, world);

            worldRegistry.add(RegistryKey.of(Registry.WORLD_KEY, new Identifier("void")), world, Lifecycle.stable());

            WorldBorder border = world.getWorldBorder();
            border.load(props.getWorldBorder());

            session.backupLevelDataFile(regManager, saveProperties, this.playerManager.getUserData());

            session.save(worldName);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
