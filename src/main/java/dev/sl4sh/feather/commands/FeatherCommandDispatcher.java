package dev.sl4sh.feather.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.sl4sh.feather.Feather;
import net.minecraft.server.command.ServerCommandSource;

public class FeatherCommandDispatcher extends CommandDispatcher<ServerCommandSource> {

    public FeatherCommandDispatcher() {

    }

    public static LiteralCommandNode<ServerCommandSource> build(final LiteralArgumentBuilder<ServerCommandSource> command) {

        final FeatherCommandNode result = new FeatherCommandNode(command.getLiteral(),
                command.getCommand(), command.getRequirement(), command.getRedirect(), command.getRedirectModifier(), command.isFork());

        for (final CommandNode<ServerCommandSource> argument : command.getArguments()) {
            result.addChild(argument);
        }

        return result;
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> register(final LiteralArgumentBuilder<ServerCommandSource> command) {

        for (var child : getRoot().getChildren()){

            if(child.getName().equals(command.getLiteral())){

                Feather.getLogger().warn("The command named {} is already registered. The first implementation will be overridden.", command.getLiteral());
                break;

            }

        }

        final var build = build(command);
        getRoot().addChild(build);

        Feather.getPermissionService().registerCommandName(command.getLiteral());

        return build;

    }

}
