package dev.sl4sh.feather.event;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.command.ServerCommandSource;

public class CommandExecutionEvent extends CancellableEvent{

    private final String fullCommand;
    private final String commandName;
    private final ServerCommandSource source;

    public CommandExecutionEvent(String fullCommand, ServerCommandSource source) {

        this.fullCommand = fullCommand;
        this.source = source;

        int index = fullCommand.indexOf(" ") - 1;

        if (index < 0){
            index = fullCommand.length() - 1    ;
        }

        this.commandName = fullCommand.replace("/", "").substring(0, index);

    }

    public String getFullCommand() {
        return fullCommand;
    }

    public ServerCommandSource getSource() {
        return source;
    }

    public String getCommandName() {
        return commandName;
    }
}
