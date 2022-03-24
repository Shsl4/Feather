package dev.sl4sh.feather.permissions;

import java.util.Arrays;

public enum Permissions {

    COMMAND_ADVANCEMENT("advancement", false),
    COMMAND_ATTRIBUTE("attribute", false),
    COMMAND_BAN("ban", false),
    COMMAND_BANIP("ban-ip", false),
    COMMAND_BANLIST("banlist", false),
    COMMAND_BOSSBAR("bossbar", false),
    COMMAND_CLEAR("clear", false),
    COMMAND_CLONE("clone", false),
    COMMAND_DATA("data", false),
    COMMAND_DATAPACK("datapack", false),
    COMMAND_DEBUG("debug", false),
    COMMAND_DEFAULTGAMEMODE("defaultgamemode", false),
    COMMAND_DEOP("deop", false),
    COMMAND_DIFFICULTY("difficulty", false),
    COMMAND_EFFECT("effect", false),
    COMMAND_ENCHANT("enchant", false),
    COMMAND_EXECUTE("execute", false),
    COMMAND_EXPERIENCE("experience", false),
    COMMAND_FILL("fill", false),
    COMMAND_FORCELOAD("forceload", false),
    COMMAND_FUNCTION("function", false),
    COMMAND_GAMEMODE("gamemode", false),
    COMMAND_GAMERULE("gamerule", false),
    COMMAND_GIVE("give", false),
    COMMAND_HELP("help", true),
    COMMAND_ITEM("item", false),
    COMMAND_KICK("kick", false),
    COMMAND_KILL("kill", false),
    COMMAND_LIST("list", false),
    COMMAND_LOCATE("locate", false),
    COMMAND_LOCATEBIOME("locatebiome", false),
    COMMAND_LOOT("loot", false),
    COMMAND_ME("me", false),
    COMMAND_MSG("msg", false),
    COMMAND_OP("op", false),
    COMMAND_PARDON("pardon", false),
    COMMAND_PARDONIP("pardon-ip", false),
    COMMAND_PARTICLE("particle", false),
    COMMAND_PERF("perf", false),
    COMMAND_PLAYSOUND("playsound", false),
    COMMAND_RECIPE("recipe", false),
    COMMAND_RELOAD("reload", false),
    COMMAND_SAVEALL("save-all", false),
    COMMAND_SAVEOFF("save-off", false),
    COMMAND_SAVEON("save-on", false),
    COMMAND_SAY("say", false),
    COMMAND_SCHEDULE("schedule", false),
    COMMAND_SCOREBOARD("scoreboard", false),
    COMMAND_SEED("seed", false),
    COMMAND_SETBLOCK("setblock", false),
    COMMAND_SETIDLETIMEOUT("setidletimeout", false),
    COMMAND_SETWORLDSPAWN("setworldspawn", false),
    COMMAND_SPAWNPOINT("spawnpoint", false),
    COMMAND_SPECTATE("spectate", false),
    COMMAND_SPREADPLAYERS("spreadplayers", false),
    COMMAND_STOP("stop", false),
    COMMAND_STOPSOUND("stopsound", false),
    COMMAND_SUMMON("summon", false),
    COMMAND_TAG("tag", false),
    COMMAND_TEAM("team", false),
    COMMAND_TEAMMSG("teammsg", false),
    COMMAND_TELEPORT("teleport", false),
    COMMAND_TELL("tell", false),
    COMMAND_TELLRAW("tellraw", false),
    COMMAND_TEST("test", false),
    COMMAND_TIME("time", false),
    COMMAND_TITLE("title", false),
    COMMAND_TM("tm", false),
    COMMAND_TP("tp", false),
    COMMAND_TRIGGER("trigger", false),
    COMMAND_W("w", false),
    COMMAND_WEATHER("weather", false),
    COMMAND_WHITELIST("whitelist", false),
    COMMAND_WORLDBORDER("worldborder", false),
    COMMAND_XP("xp", false);

    private final String id;
    private final boolean defaultValue;

    Permissions(String s, boolean b) {
        id = s;
        defaultValue = b;
    }

    public String getId() {
        return id;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public static boolean getCommandDefaultValue(String command){

        var p = Arrays.stream(Permissions.values()).filter(v -> v.id.equals(command)).findFirst();
        return p.isPresent() && p.get().defaultValue;

    }

}
