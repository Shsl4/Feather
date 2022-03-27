package dev.sl4sh.feather.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.CommandRegistrationEvent;
import dev.sl4sh.feather.event.player.*;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.event.registration.EventResponder;
import dev.sl4sh.feather.event.registration.Register;
import dev.sl4sh.feather.permissions.Permission;
import dev.sl4sh.feather.util.Utilities;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@EventResponder
@Environment(EnvType.SERVER)
public class FeatherServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {

    }

    @Register
    public static void register(EventRegistry registry) {

        registry.POST_CONNECT.register(FeatherServer::onConnect);
        registry.POST_DISCONNECT.register(FeatherServer::onDisconnect);
        registry.POST_DEATH.register(FeatherServer::onDeath);
        registry.POST_RESPAWN.register(FeatherServer::onRespawn);
        registry.POST_TELEPORT.register(FeatherServer::onTeleport);
        registry.POST_DIM_CHANGE.register(FeatherServer::onDimensionChange);

        registry.COMMAND_REGISTRATION.register(FeatherServer::registerCommands);

    }

    public static final SuggestionProvider<ServerCommandSource> COMMAND_SUGGESTION = ((context, builder) -> CommandSource.suggestMatching(Feather.getPermissionManager().getRegisteredCommandNames(), builder));

    public static final SuggestionProvider<ServerCommandSource> GROUP_SUGGESTION = ((context, builder) -> {

        List<String> suggestions = new ArrayList<>();

        for (Permission.Group group : Feather.getPermissionManager().getGroups()) {
            suggestions.add(group.getName());
        }

        return CommandSource.suggestMatching(suggestions, builder);

    });

    public static final SuggestionProvider<ServerCommandSource> MEMBER_SUGGESTION = ((context, builder) -> {

        String name = StringArgumentType.getString(context, "name");

        Optional<Permission.Group> group = Feather.getPermissionManager().getGroup(name);

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

        Optional<Permission.Group> group = Feather.getPermissionManager().getGroup(name);

        if (group.isPresent()) {

            if (group.get().getUsers().containsKey(uuid)){
                Utilities.sendWarning(source, playerName + " is already a member of the group.");
                return 0;
            }

            group.get().addMember(player);

            source.getServer().getCommandManager().sendCommandTree(player);

            source.sendFeedback(Text.of(String.format("\u00a7aSuccessfully added user %s to group %s.",
                    player.getName().asString(),
                    group.get().getDisplayName().asString())), false);

        } else {

            source.sendError(Text.of("The group named " + name + " does not exist."));
            return 1;

        }

        return 0;

    }

    private static int removeUser(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        String user = StringArgumentType.getString(context, "user");

        Optional<Permission.Group> group = Feather.getPermissionManager().getGroup(name);

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


    private static int listPermissions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        List<String> permissions = Feather.getPermissionManager().getRegisteredCommandNames();
        source.sendFeedback(Text.of("\u00a72" + String.join(", ", permissions)), false);

        return 0;

    }

    private static int createGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        Feather.getPermissionManager().createGroup(source, name, Text.of(name));

        return 0;

    }

    private static int deleteGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        Feather.getPermissionManager().deleteGroup(source, name);

        return 0;

    }

    private static int infoGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        Optional<Permission.Group> optionalGroup = Feather.getPermissionManager().getGroup(name);

        if (optionalGroup.isPresent()) {

            Permission.Group group = optionalGroup.get();

            source.sendFeedback(Text.of(String.format("\u00a76| %s\u00a76 Members |\n", group.getDisplayName().asString())), false);

            if (group.getUsers().size() > 0){

                for (UUID id : group.getUsers().keySet()) {

                    String playerName = group.getUsers().get(id);

                    source.sendFeedback(Text.of(String.format("\u00a7fName: \u00a77%s \u00a7f| UUID: \u00a77%s", playerName, id.toString())), false);

                }

            }
            else{
                source.sendFeedback(Text.of("\u00a77The group has no members yet."), false);
            }

            source.sendFeedback(Text.of(String.format("\n\u00a76| %s\u00a76 Permissions |\n", group.getDisplayName().asString())), false);

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

            Feather.getPermissionManager().grantPermission(context.getSource(), permission, player);

        } else {

            Feather.getPermissionManager().revokePermission(context.getSource(), permission, player);

        }

        return 0;

    }

    private static int setGroupPermission(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        String permission = StringArgumentType.getString(context, "permission");
        String name = StringArgumentType.getString(context, "name");
        boolean value = BoolArgumentType.getBool(context, "value");

        if (value) {
            Feather.getPermissionManager().grantPermission(context.getSource(), permission, name);
        } else {
            Feather.getPermissionManager().revokePermission(context.getSource(), permission, name);
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
