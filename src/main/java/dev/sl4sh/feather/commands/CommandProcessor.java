package dev.sl4sh.feather.commands;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.CommandExecutionEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.event.registration.EventResponder;
import dev.sl4sh.feather.event.registration.Register;
import dev.sl4sh.feather.permissions.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.List;

@EventResponder
public class CommandProcessor {

    @Register
    public static void register(EventRegistry registry){

        registry.COMMAND_EXECUTION.register(CommandProcessor::onCommand);

    }

    private static void onCommand(CommandExecutionEvent event){

        if(event.getSource().getEntity() instanceof ServerPlayerEntity player){

            if(!Feather.getPermissionManager().hasPermission(event.getCommandId(), player)){

                event.setCancelled(true, "You are not allowed to run this command.");

            }

        }

    }



}
