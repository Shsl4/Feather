package dev.sl4sh.feather.event;

import net.minecraft.server.command.ServerCommandSource;

public class CommandExecutionEvent extends CancellableEvent{

    private final String commandName;
    private final ServerCommandSource source;


    public CommandExecutionEvent(String commandName, ServerCommandSource source) {
        this.commandName = commandName;
        this.source = source;
    }

    public String getCommandName() {
        return commandName;
    }

    public ServerCommandSource getSource() {
        return source;
    }
}
