package dev.sl4sh.feather.commands;

import dev.sl4sh.feather.event.CommandExecutionEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.event.registration.EventResponder;
import dev.sl4sh.feather.event.registration.Register;

import java.util.Arrays;
import java.util.List;

@EventResponder
public class CommandProcessor {

    public static final List<String> MINECRAFT_COMMANDS = Arrays.asList("advancement", "attribute", "ban", "ban-ip",
            "banlist", "bossbar", "clear", "clone", "data", "datapack", "debug", "defaultgamemode", "deop",
            "difficulty", "effect", "enchant", "execute", "experience", "fill", "forceload", "function", "gamemode",
            "gamerule", "give", "help", "item", "kick", "kill", "list", "locate", "locatebiome",
            "loot", "me", "msg", "op", "pardon", "pardon-ip", "particle", "perf", "playsound", "recipe",
            "reload", "save-all", "save-off", "save-on", "say", "schedule", "scoreboard", "seed", "setblock",
            "setidletimeout", "setworldspawn", "spawnpoint", "spectate", "spreadplayers", "stop", "stopsound",
            "summon", "tag", "team", "teammsg", "teleport", "tell", "tellraw", "test", "time", "title", "tm",
            "tp", "trigger", "w", "weather", "whitelist", "worldborder", "xp");

    @Register
    public static void register(EventRegistry registry){

        registry.COMMAND_EXECUTION.register(CommandProcessor::onCommand);

    }

    private static void onCommand(CommandExecutionEvent event){

        //event.setCancelled(true, ("You are not allowed to use " + event.getCommandName()));

    }



}
