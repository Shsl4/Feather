package dev.sl4sh.feather.commands;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.CommandExecutionEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandProcessor {

    public static void register(EventRegistry registry){

        registry.COMMAND_EXECUTION.register(CommandProcessor::onCommand);

    }

    private static void onCommand(CommandExecutionEvent event){

        if(event.getSource().getEntity() instanceof ServerPlayerEntity player){

            if(!Feather.getPermissionService().hasPermission(event.getCommandName(), player)){

                event.setCancelled(true, "You are not allowed to run this command.");

            }

        }

    }



}
