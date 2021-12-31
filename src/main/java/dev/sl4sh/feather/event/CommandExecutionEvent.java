package dev.sl4sh.feather.event;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.commands.CommandProcessor;
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

    public String getCommandId(){

        return Feather.getPermissionManager().getCommandPermission(commandName.replace("/", "")).orElse("");

    }

    public ServerCommandSource getSource() {
        return source;
    }
}
