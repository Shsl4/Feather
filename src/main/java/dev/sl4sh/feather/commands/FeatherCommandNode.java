package dev.sl4sh.feather.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.sl4sh.feather.Feather;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

public class FeatherCommandNode extends LiteralCommandNode<ServerCommandSource> {
    public FeatherCommandNode(String literal, Command<ServerCommandSource> command, Predicate<ServerCommandSource> requirement, CommandNode<ServerCommandSource> redirect, RedirectModifier<ServerCommandSource> modifier, boolean forks) {
        super(literal, command, requirement, redirect, modifier, forks);
    }

    @Override
    public boolean canUse(final ServerCommandSource source){

        try {
            // Check if the player has permission to use the command
            return Feather.getPermissionManager().hasPermission(getLiteral(), source.getPlayer());
        } catch (CommandSyntaxException e) {

            // If the block throws, the source is not a player and only the console is allowed to use any command.
            return source.getEntity() == null;
        }

    }
}
