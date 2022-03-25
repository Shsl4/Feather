package dev.sl4sh.feather.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@EventResponder
@Environment(EnvType.SERVER)
public class FeatherServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {

    }

    @Register
    public static void register(EventRegistry registry){

        registry.POST_CONNECT.register(FeatherServer::onConnect);
        registry.POST_DISCONNECT.register(FeatherServer::onDisconnect);
        registry.POST_DEATH.register(FeatherServer::onDeath);
        registry.POST_RESPAWN.register(FeatherServer::onRespawn);
        registry.POST_TELEPORT.register(FeatherServer::onTeleport);
        registry.POST_DIM_CHANGE.register(FeatherServer::onDimensionChange);

        registry.COMMAND_REGISTRATION.register(FeatherServer::registerCommands);

    }

    public static final SuggestionProvider<ServerCommandSource> COMMAND_SUGGESTION = ((context, builder) -> CommandSource.suggestMatching(Feather.getPermissionManager().getRegisteredCommandNames(), builder));

    public static final SuggestionProvider<ServerCommandSource> TYPE_SUGGESTION = ((context, builder) -> {

        Collection<String> commands = new ArrayList<>();
        commands.add("user");
        commands.add("group");

        return CommandSource.suggestMatching(commands, builder);

    });

    private static void registerCommands(CommandRegistrationEvent event) {

        event.register(CommandManager.literal("permission")
                .then(CommandManager.literal("set"))
                .then(CommandManager.argument("type", StringArgumentType.word()).suggests(TYPE_SUGGESTION)
                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                .then(CommandManager.argument("name", StringArgumentType.word()).suggests(COMMAND_SUGGESTION)
                .then(CommandManager.argument("value", BoolArgumentType.bool())
                .executes(context -> {

                    Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                    UUID uuid = profiles.iterator().next().getId();
                    ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(uuid);
                    String name = StringArgumentType.getString(context, "name");
                    String type = StringArgumentType.getString(context, "type");
                    boolean value = BoolArgumentType.getBool(context, "value");

                    if (type.equals("user")){
                        if (value){
                            Feather.getPermissionManager().grantPermission(context.getSource(), name, player);
                        }
                        else{
                            Feather.getPermissionManager().revokePermission(context.getSource(), name, player);
                        }
                    }
                    else if (type.equals("group")){
                        if (value){
                            Feather.getPermissionManager().grantPermission(context.getSource(), name, player);
                        }
                        else{
                            Feather.getPermissionManager().revokePermission(context.getSource(), name, player);
                        }
                    }
                    else{

                    }

                    return 0;

                }))))));

    }

    private static void onConnect(PlayerPostConnectEvent event){

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
