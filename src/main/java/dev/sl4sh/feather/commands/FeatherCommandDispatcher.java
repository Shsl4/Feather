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

        for (var child : getRoot().getChildren()){

            if(child.getName().equals(command.getLiteral())){

                Feather.getLogger().warn("The command named {} is already registered. The first implementation will be overridden.", command.getLiteral());
                break;

            }

        }

        final var build = command.build();
        getRoot().addChild(build);

        Feather.getPermissionManager().registerCommandName(command.getLiteral());

        return build;

    }

}
