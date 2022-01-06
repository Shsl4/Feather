package dev.sl4sh.feather.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.sl4sh.feather.Feather;
import net.fabricmc.fabric.impl.gametest.FabricGameTestModInitializer;
import net.fabricmc.fabric.impl.resource.loader.FabricModResourcePack;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class FeatherCommandDispatcher extends CommandDispatcher<ServerCommandSource> {

    public FeatherCommandDispatcher() {

    }

    @Override
    public LiteralCommandNode<ServerCommandSource> register(final LiteralArgumentBuilder<ServerCommandSource> command) {

        final var build = command.build();
        getRoot().addChild(build);
        Feather.getPermissionManager().setupCommandPermission(build.getLiteral());

        return build;

    }

    public LiteralCommandNode<ServerCommandSource> register(String prefix, final LiteralArgumentBuilder<ServerCommandSource> command) {

        for (var child : getRoot().getChildren()){

            if(child.getName().equals(command.getLiteral())){

                Feather.getLogger().warn("The command named {} is already registered. Registering with prefix {}:{}.", command.getLiteral(), prefix, command.getLiteral());
                final var build = CommandManager.literal(prefix + ":" + command.getLiteral()).executes(command.getCommand()).build();
                getRoot().addChild(build);

                Feather.getPermissionManager().setupCommandPermission(build.getLiteral());

                return build;

            }

        }

        var build = command.build();
        final var alias = CommandManager.literal(prefix + ":" + command.getLiteral()).redirect(build).build();

        Feather.getLogger().info(Thread.currentThread().getStackTrace()[2].getClassName() + " is registering " + command.getLiteral());

        var l = getClass().getClassLoader().getResourceAsStream("fabric.mod.json");

        getRoot().addChild(build);
        getRoot().addChild(alias);

        Feather.getLogger().info("Registered command {} with alias {}.", build.getLiteral(), alias.getLiteral());
        Feather.getPermissionManager().setupCommandPermission(alias.getLiteral());

        return build;
    }

}
