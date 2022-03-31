package dev.sl4sh.feather.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Lifecycle;
import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.MinecraftServerInterface;
import dev.sl4sh.feather.commands.CommandProcessor;
import dev.sl4sh.feather.event.CommandRegistrationEvent;
import dev.sl4sh.feather.event.player.*;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.Permission;
import dev.sl4sh.feather.mixin.MinecraftServerMixin;
import dev.sl4sh.feather.util.Utilities;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Environment(EnvType.SERVER)
public class FeatherServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {

        FeatherServer.register(Feather.getEventRegistry());
        CommandProcessor.register(Feather.getEventRegistry());

    }

    public static void register(EventRegistry registry) {

        registry.POST_CONNECT.register(FeatherServer::onConnect);
        registry.POST_DISCONNECT.register(FeatherServer::onDisconnect);
        registry.POST_DEATH.register(FeatherServer::onDeath);
        registry.POST_RESPAWN.register(FeatherServer::onRespawn);
        registry.POST_TELEPORT.register(FeatherServer::onTeleport);
        registry.POST_DIM_CHANGE.register(FeatherServer::onDimensionChange);

        registry.COMMAND_REGISTRATION.register(FeatherServer::registerCommands);

    }

    public static final SuggestionProvider<ServerCommandSource> COMMAND_SUGGESTION = ((context, builder) -> CommandSource.suggestMatching(Feather.getPermissionService().getRegisteredCommandNames(), builder));

    public static final SuggestionProvider<ServerCommandSource> GROUP_SUGGESTION = ((context, builder) -> {

        List<String> suggestions = new ArrayList<>();

        for (Permission.Group group : Feather.getPermissionService().getGroups()) {
            suggestions.add(group.getName());
        }

        return CommandSource.suggestMatching(suggestions, builder);

    });
    public static final SuggestionProvider<ServerCommandSource> DIMENSION_SUGGESTION = ((context, builder) -> {

        List<String> suggestions = new ArrayList<>();

        for (ServerWorld world : context.getSource().getServer().getWorlds()) {
            suggestions.add(Utilities.getWorldDimensionName(world));
        }

        return CommandSource.suggestMatching(suggestions, builder);

    });

    public static final SuggestionProvider<ServerCommandSource> MEMBER_SUGGESTION = ((context, builder) -> {

        String name = StringArgumentType.getString(context, "name");

        Optional<Permission.Group> group = Feather.getPermissionService().getGroup(name);

        if (group.isPresent()) {
            return CommandSource.suggestMatching(group.get().getUsers().values(), builder);
        }

        return CommandSource.suggestMatching(new ArrayList<>(), builder);

    });

    private static int addPlayerToGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "user");
        GameProfile profile = profiles.iterator().next();
        UUID uuid = profile.getId();
        String playerName = profile.getName();
        ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(uuid);

        if (player == null) {
            source.sendError(Text.of("The user " + playerName + " could not be found. They might be offline."));
            return 1;
        }

        Optional<Permission.Group> group = Feather.getPermissionService().getGroup(name);

        if (group.isPresent()) {

            if (group.get().getUsers().containsKey(uuid)){
                Utilities.sendWarning(source, playerName + " is already a member of the group.");
                return 0;
            }

            group.get().addMember(player);

            source.getServer().getCommandManager().sendCommandTree(player);

            source.sendFeedback(Text.of(String.format("\u00a7aSuccessfully added user %s to group %s.",
                    player.getName().asString(),
                    group.get().getDisplayName())), false);

        } else {

            source.sendError(Text.of("The group named " + name + " does not exist."));
            return 1;

        }

        return 0;

    }

    private static int removeUser(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        String user = StringArgumentType.getString(context, "user");

        Optional<Permission.Group> group = Feather.getPermissionService().getGroup(name);

        if (group.isPresent()) {

            if (group.get().removeMember(user)) {

                ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(user);

                if (player != null) {
                    source.getServer().getCommandManager().sendCommandTree(player);
                }

                source.sendFeedback(Text.of("\u00a7l\u00a7c" + user + " was removed from the group."), false);

            } else {

                source.sendError(Text.of(user + " is not a member of the group."));

            }
        }

        return 0;

    }


    private static int listPermissions(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        List<String> permissions = Feather.getPermissionService().getRegisteredCommandNames();
        source.sendFeedback(Text.of("\u00a72" + String.join(", ", permissions)), false);

        return 0;

    }

    private static int createGroup(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        Feather.getPermissionService().createGroup(source, name, name);

        return 0;

    }

    private static int deleteGroup(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        Feather.getPermissionService().deleteGroup(source, name);

        return 0;

    }

    private static int infoGroup(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        Optional<Permission.Group> optionalGroup = Feather.getPermissionService().getGroup(name);

        if (optionalGroup.isPresent()) {

            Permission.Group group = optionalGroup.get();

            source.sendFeedback(Text.of(String.format("\u00a76| %s\u00a76 Members |\n", group.getDisplayName())), false);

            if (group.getUsers().size() > 0){

                for (UUID id : group.getUsers().keySet()) {

                    String playerName = group.getUsers().get(id);

                    source.sendFeedback(Text.of(String.format("\u00a7fName: \u00a77%s \u00a7f| UUID: \u00a77%s", playerName, id.toString())), false);

                }

            }
            else{
                source.sendFeedback(Text.of("\u00a77The group has no members yet."), false);
            }

            source.sendFeedback(Text.of(String.format("\n\u00a76| %s\u00a76 Permissions |\n", group.getDisplayName())), false);

            if (group.getPermissions().size() > 0){

                for (Permission.Entry entry : group.getPermissions()) {

                    if (entry.getState()) {

                        source.sendFeedback(Text.of(String.format("\u00a7fPermission: \u00a77%s \u00a7f| State: \u00a7atrue", entry.getPermission())), false);

                    } else {

                        source.sendFeedback(Text.of(String.format("\u00a7fPermission: \u00a77%s \u00a7f| State: \u00a7cfalse", entry.getPermission())), false);

                    }

                }

            }
            else{

                source.sendFeedback(Text.of("\u00a77The group has no permission set.\n"), false);

            }

        }

        return 0;

    }

    private static int setPlayerPermission(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "user");
        UUID uuid = profiles.iterator().next().getId();
        ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(uuid);
        String permission = StringArgumentType.getString(context, "permission");
        boolean value = BoolArgumentType.getBool(context, "value");

        if (value) {

            Feather.getPermissionService().grantPermission(context.getSource(), permission, player);

        } else {

            Feather.getPermissionService().revokePermission(context.getSource(), permission, player);

        }

        return 0;

    }

    private static int setGroupPermission(CommandContext<ServerCommandSource> context) {

        String permission = StringArgumentType.getString(context, "permission");
        String name = StringArgumentType.getString(context, "name");
        boolean value = BoolArgumentType.getBool(context, "value");

        if (value) {
            Feather.getPermissionService().grantPermission(context.getSource(), permission, name);
        } else {
            Feather.getPermissionService().revokePermission(context.getSource(), permission, name);
        }

        return 0;

    }

    private static int heal(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        try {

            Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "user");

            PlayerManager manager = context.getSource().getServer().getPlayerManager();

            for (GameProfile profile : profiles){

                ServerPlayerEntity player = manager.getPlayer(profile.getId());

                if (player != null){
                    Utilities.restoreHealth(player);
                }

            }


        }
        catch (IllegalArgumentException e){

            try{

                ServerPlayerEntity player = context.getSource().getPlayer();
                Utilities.restoreHealth(player);

            }
            catch (CommandSyntaxException e2){

                Utilities.sendError(context.getSource(), "This command can only be used by players.");

            }

        }
        return 0;

    }

    private static int feed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        try {

            Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "user");

            PlayerManager manager = context.getSource().getServer().getPlayerManager();

            for (GameProfile profile : profiles){

                ServerPlayerEntity player = manager.getPlayer(profile.getId());

                if (player != null){
                    Utilities.feed(player);
                }

            }

        }
        catch (IllegalArgumentException e){

            try{

                ServerPlayerEntity player = context.getSource().getPlayer();
                Utilities.feed(player);

            }
            catch (CommandSyntaxException e2){

                Utilities.sendError(context.getSource(), "This command can only be used by players.");

            }

        }

        return 0;

    }

    private static int empty(CommandContext<ServerCommandSource> context) {

        context.getSource().sendError(Text.of("This command is not implemented."));
        return 0;

    }

    private static void registerCommands(CommandRegistrationEvent event) {

        event.register(CommandManager.literal("permission")

                .then(CommandManager.literal("group")

                        .then(CommandManager.argument("name", StringArgumentType.word()).suggests(GROUP_SUGGESTION)

                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("permission", StringArgumentType.string()).suggests(COMMAND_SUGGESTION)
                                                .then(CommandManager.argument("value", BoolArgumentType.bool())
                                                        .executes(FeatherServer::setGroupPermission))))

                                .then(CommandManager.literal("delete")
                                        .executes(FeatherServer::deleteGroup))

                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument("user", GameProfileArgumentType.gameProfile())
                                                .executes(FeatherServer::addPlayerToGroup)))

                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("user", StringArgumentType.string()).suggests(MEMBER_SUGGESTION)
                                                .executes(FeatherServer::removeUser)))

                                .then(CommandManager.literal("info")
                                        .executes(FeatherServer::infoGroup)))

                        .then(CommandManager.literal("create")
                                .then(CommandManager.argument("name", StringArgumentType.word())
                                        .executes(FeatherServer::createGroup)))

                        .then(CommandManager.literal("list")
                                .executes(FeatherServer::empty)))

                .then(CommandManager.literal("user")

                        .then(CommandManager.argument("user", GameProfileArgumentType.gameProfile())

                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("permission", StringArgumentType.string()).suggests(COMMAND_SUGGESTION)
                                                .then(CommandManager.argument("value", BoolArgumentType.bool())
                                                        .executes(FeatherServer::setPlayerPermission))))

                                .then(CommandManager.literal("info")
                                        .executes(FeatherServer::empty))))

                .then(CommandManager.literal("list")
                        .executes(FeatherServer::listPermissions))

        );

        event.register(CommandManager.literal("heal")
                .then(CommandManager.argument("user", GameProfileArgumentType.gameProfile())
                        .executes(FeatherServer::heal))
                .executes(FeatherServer::heal));

        event.register(CommandManager.literal("feed")
                .then(CommandManager.argument("user", GameProfileArgumentType.gameProfile())
                        .executes(FeatherServer::feed))
                .executes(FeatherServer::feed));

        event.register(CommandManager.literal("feather")
                .then(CommandManager.literal("reload")
                        .executes((context -> {

                            Feather.getPermissionService().loadConfiguration();

                            for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()){
                                context.getSource().getServer().getCommandManager().sendCommandTree(player);
                            }

                            Utilities.sendSuccess(context.getSource(), "Reloaded JSON configuration files.");
                            return 0;

                        }))));

        event.register(CommandManager.literal("back")
                .executes(context -> {

                    ServerPlayerEntity player = context.getSource().getPlayer();

                    if (!Feather.getBackService().back(player)){
                        Utilities.sendWarning(context.getSource(), "You do not have any previous death location.");
                    }

                    return 0;

                }));

        event.register(CommandManager.literal("break")
                .executes(context -> {

                    try{

                        ((MinecraftServerInterface)context.getSource().getServer()).createWorld();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    return 0;

                }));

        event.register(CommandManager.literal("travel")
                .then(CommandManager.argument("dimension", StringArgumentType.string()).suggests(DIMENSION_SUGGESTION)
                        .executes(context -> {

                            ServerPlayerEntity player = context.getSource().getPlayer();
                            String dimension = StringArgumentType.getString(context, "dimension");
                            Optional<ServerWorld> world = Utilities.getWorldByName(context.getSource().getServer(), dimension);

                            if (world.isPresent()){

                                BlockPos pos = world.get().getSpawnPos();
                                player.teleport(world.get(), pos.getX(), pos.getY(), pos.getZ(), world.get().getSpawnAngle(), 0.0f);

                            }
                            else {

                                Utilities.sendError(context.getSource(), "The dimension you're trying to travel to does not exist.");

                            }

                            return 0;

                        })));

    }

    private static void onConnect(PlayerPostConnectEvent event) {

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        Feather.getLogger().info("{} connected to the server on {} at {}.",
                event.getPlayer().getName().asString(),
                dateTime.format(dateFormatter),
                dateTime.format(timeFormatter));

    }

    private static void onDisconnect(PlayerPostDisconnectEvent event) {

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Feather.getLogger().info("{} disconnected from the server on {} at {}.",
                event.getPlayer().getName().asString(),
                dateTime.format(dateFormatter),
                dateTime.format(timeFormatter));

    }

    private static void onDeath(PlayerPostDeathEvent event) {

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Feather.getLogger().info("{} died at coordinates {} in dimension {} on {} at {}",
                event.getPlayer().getName().asString(),
                event.getPlayer().getPos(),
                Utilities.getNiceWorldDimensionName(event.getPlayer().getWorld()),
                dateTime.format(dateFormatter),
                dateTime.format(timeFormatter));

    }

    private static void onRespawn(PlayerPostRespawnEvent event) {

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ServerPlayerEntity player = event.getPlayer();

        Feather.getLogger().info("{} respawned from dimension {} to dimension {} at coordinates {} on {} at {}",
                player.getName().asString(),
                Utilities.getNiceWorldDimensionName(player.getWorld()),
                Utilities.getNiceWorldDimensionName(event.getWorld()),
                player.getPos(),
                dateTime.format(dateFormatter),
                dateTime.format(timeFormatter));

    }

    private static void onTeleport(PlayerPostTeleportEvent event) {

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ServerPlayerEntity player = event.getPlayer();

        Feather.getLogger().info("{} teleported to coordinates {} in dimension {} on {} at {}",
                player.getName().asString(),
                player.getPos(),
                Utilities.getNiceWorldDimensionName(player.getWorld()),
                dateTime.format(dateFormatter),
                dateTime.format(timeFormatter));


    }

    private static void onDimensionChange(PlayerPostDimensionChangeEvent event) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ServerPlayerEntity player = event.getPlayer();

        Feather.getLogger().info("{} travelled from dimension {} to dimension {} at coordinates {} on {} at {}",
                player.getName().asString(),
                Utilities.getNiceWorldDimensionName(event.getOrigin()),
                Utilities.getNiceWorldDimensionName(player.getWorld()),
                player.getPos(),
                dateTime.format(dateFormatter),
                dateTime.format(timeFormatter));
    }

}
