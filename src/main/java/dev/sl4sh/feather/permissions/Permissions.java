package dev.sl4sh.feather.permissions;

import java.util.Arrays;

public enum Permissions {

    COMMAND_ADVANCEMENT("command.advancement", false),
    COMMAND_ATTRIBUTE("command.attribute", false),
    COMMAND_BAN("command.ban", false),
    COMMAND_BANIP("command.ban-ip", false),
    COMMAND_BANLIST("command.banlist", false),
    COMMAND_BOSSBAR("command.bossbar", false),
    COMMAND_CLEAR("command.clear", false),
    COMMAND_CLONE("command.clone", false),
    COMMAND_DATA("command.data", false),
    COMMAND_DATAPACK("command.datapack", false),
    COMMAND_DEBUG("command.debug", false),
    COMMAND_DEFAULTGAMEMODE("command.defaultgamemode", false),
    COMMAND_DEOP("command.deop", false),
    COMMAND_DIFFICULTY("command.difficulty", false),
    COMMAND_EFFECT("command.effect", false),
    COMMAND_ENCHANT("command.enchant", false),
    COMMAND_EXECUTE("command.execute", false),
    COMMAND_EXPERIENCE("command.experience", false),
    COMMAND_FILL("command.fill", false),
    COMMAND_FORCELOAD("command.forceload", false),
    COMMAND_FUNCTION("command.function", false),
    COMMAND_GAMEMODE("command.gamemode", false),
    COMMAND_GAMERULE("command.gamerule", false),
    COMMAND_GIVE("command.give", false),
    COMMAND_HELP("command.help", true),
    COMMAND_ITEM("command.item", false),
    COMMAND_KICK("command.kick", false),
    COMMAND_KILL("command.kill", false),
    COMMAND_LIST("command.list", false),
    COMMAND_LOCATE("command.locate", false),
    COMMAND_LOCATEBIOME("command.locatebiome", false),
    COMMAND_LOOT("command.loot", false),
    COMMAND_ME("command.me", false),
    COMMAND_MSG("command.msg", false),
    COMMAND_OP("command.op", false),
    COMMAND_PARDON("command.pardon", false),
    COMMAND_PARDONIP("command.pardon-ip", false),
    COMMAND_PARTICLE("command.particle", false),
    COMMAND_PERF("command.perf", false),
    COMMAND_PLAYSOUND("command.playsound", false),
    COMMAND_RECIPE("command.recipe", false),
    COMMAND_RELOAD("command.reload", false),
    COMMAND_SAVEALL("command.save-all", false),
    COMMAND_SAVEOFF("command.save-off", false),
    COMMAND_SAVEON("command.save-on", false),
    COMMAND_SAY("command.say", false),
    COMMAND_SCHEDULE("command.schedule", false),
    COMMAND_SCOREBOARD("command.scoreboard", false),
    COMMAND_SEED("command.seed", false),
    COMMAND_SETBLOCK("command.setblock", false),
    COMMAND_SETIDLETIMEOUT("command.setidletimeout", false),
    COMMAND_SETWORLDSPAWN("command.setworldspawn", false),
    COMMAND_SPAWNPOINT("command.spawnpoint", false),
    COMMAND_SPECTATE("command.spectate", false),
    COMMAND_SPREADPLAYERS("command.spreadplayers", false),
    COMMAND_STOP("command.stop", false),
    COMMAND_STOPSOUND("command.stopsound", false),
    COMMAND_SUMMON("command.summon", false),
    COMMAND_TAG("command.tag", false),
    COMMAND_TEAM("command.team", false),
    COMMAND_TEAMMSG("command.teammsg", false),
    COMMAND_TELEPORT("command.teleport", false),
    COMMAND_TELL("command.tell", false),
    COMMAND_TELLRAW("command.tellraw", false),
    COMMAND_TEST("command.test", false),
    COMMAND_TIME("command.time", false),
    COMMAND_TITLE("command.title", false),
    COMMAND_TM("command.tm", false),
    COMMAND_TP("command.tp", false),
    COMMAND_TRIGGER("command.trigger", false),
    COMMAND_W("command.w", false),
    COMMAND_WEATHER("command.weather", false),
    COMMAND_WHITELIST("command.whitelist", false),
    COMMAND_WORLDBORDER("command.worldborder", false),
    COMMAND_XP("command.xp", false);

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
