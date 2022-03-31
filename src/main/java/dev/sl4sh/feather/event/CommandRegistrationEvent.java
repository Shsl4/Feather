package dev.sl4sh.feather.event;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.sl4sh.feather.commands.FeatherCommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistrationEvent implements FeatherEvent {

    private final FeatherCommandDispatcher dispatcher;
    private final CommandManager.RegistrationEnvironment environment;

    public CommandRegistrationEvent(FeatherCommandDispatcher dispatcher, CommandManager.RegistrationEnvironment environment) {
        this.dispatcher = dispatcher;
        this.environment = environment;
    }

    public CommandManager.RegistrationEnvironment getEnvironment() {
        return environment;
    }

    public LiteralCommandNode<ServerCommandSource> register(final LiteralArgumentBuilder<ServerCommandSource> command){

        return dispatcher.register(command);

    }

}
