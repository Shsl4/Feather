package dev.sl4sh.feather.permissions;

public enum Permissions {

    COMMAND_ADVANCEMENT("minecraft.command.advancement", false),
    COMMAND_ATTRIBUTE("minecraft.command.attribute", false),
    COMMAND_BAN("minecraft.command.ban", false),
    COMMAND_BANIP("minecraft.command.ban-ip", false),
    COMMAND_BANLIST("minecraft.command.banlist", false),
    COMMAND_BOSSBAR("minecraft.command.bossbar", false),
    COMMAND_CLEAR("minecraft.command.clear", false),
    COMMAND_CLONE("minecraft.command.clone", false),
    COMMAND_DATA("minecraft.command.data", false),
    COMMAND_DATAPACK("minecraft.command.datapack", false),
    COMMAND_DEBUG("minecraft.command.debug", false),
    COMMAND_DEFAULTGAMEMODE("minecraft.command.defaultgamemode", false),
    COMMAND_DEOP("minecraft.command.deop", false),
    COMMAND_DIFFICULTY("minecraft.command.difficulty", false),
    COMMAND_EFFECT("minecraft.command.effect", false),
    COMMAND_ENCHANT("minecraft.command.enchant", false),
    COMMAND_EXECUTE("minecraft.command.execute", false),
    COMMAND_EXPERIENCE("minecraft.command.experience", false),
    COMMAND_FILL("minecraft.command.fill", false),
    COMMAND_FORCELOAD("minecraft.command.forceload", false),
    COMMAND_FUNCTION("minecraft.command.function", false),
    COMMAND_GAMEMODE("minecraft.command.gamemode", false),
    COMMAND_GAMERULE("minecraft.command.gamerule", false),
    COMMAND_GIVE("minecraft.command.give", false),
    COMMAND_HELP("minecraft.command.help", true),
    COMMAND_ITEM("minecraft.command.item", false),
    COMMAND_KICK("minecraft.command.kick", false),
    COMMAND_KILL("minecraft.command.kill", false),
    COMMAND_LIST("minecraft.command.list", false),
    COMMAND_LOCATE("minecraft.command.locate", false),
    COMMAND_LOCATEBIOME("minecraft.command.locatebiome", false),
    COMMAND_LOOT("minecraft.command.loot", false),
    COMMAND_ME("minecraft.command.me", false),
    COMMAND_MSG("minecraft.command.msg", false),
    COMMAND_OP("minecraft.command.op", false),
    COMMAND_PARDON("minecraft.command.pardon", false),
    COMMAND_PARDONIP("minecraft.command.pardon-ip", false),
    COMMAND_PARTICLE("minecraft.command.particle", false),
    COMMAND_PERF("minecraft.command.perf", false),
    COMMAND_PLAYSOUND("minecraft.command.playsound", false),
    COMMAND_RECIPE("minecraft.command.recipe", false),
    COMMAND_RELOAD("minecraft.command.reload", false),
    COMMAND_SAVEALL("minecraft.command.save-all", false),
    COMMAND_SAVEOFF("minecraft.command.save-off", false),
    COMMAND_SAVEON("minecraft.command.save-on", false),
    COMMAND_SAY("minecraft.command.say", false),
    COMMAND_SCHEDULE("minecraft.command.schedule", false),
    COMMAND_SCOREBOARD("minecraft.command.scoreboard", false),
    COMMAND_SEED("minecraft.command.seed", false),
    COMMAND_SETBLOCK("minecraft.command.setblock", false),
    COMMAND_SETIDLETIMEOUT("minecraft.command.setidletimeout", false),
    COMMAND_SETWORLDSPAWN("minecraft.command.setworldspawn", false),
    COMMAND_SPAWNPOINT("minecraft.command.spawnpoint", false),
    COMMAND_SPECTATE("minecraft.command.spectate", false),
    COMMAND_SPREADPLAYERS("minecraft.command.spreadplayers", false),
    COMMAND_STOP("minecraft.command.stop", false),
    COMMAND_STOPSOUND("minecraft.command.stopsound", false),
    COMMAND_SUMMON("minecraft.command.summon", false),
    COMMAND_TAG("minecraft.command.tag", false),
    COMMAND_TEAM("minecraft.command.team", false),
    COMMAND_TEAMMSG("minecraft.command.teammsg", false),
    COMMAND_TELEPORT("minecraft.command.teleport", false),
    COMMAND_TELL("minecraft.command.tell", false),
    COMMAND_TELLRAW("minecraft.command.tellraw", false),
    COMMAND_TEST("minecraft.command.test", false),
    COMMAND_TIME("minecraft.command.time", false),
    COMMAND_TITLE("minecraft.command.title", false),
    COMMAND_TM("minecraft.command.tm", false),
    COMMAND_TP("minecraft.command.tp", false),
    COMMAND_TRIGGER("minecraft.command.trigger", false),
    COMMAND_W("minecraft.command.w", false),
    COMMAND_WEATHER("minecraft.command.weather", false),
    COMMAND_WHITELIST("minecraft.command.whitelist", false),
    COMMAND_WORLDBORDER("minecraft.command.worldborder", false),
    COMMAND_XP("minecraft.command.xp", false);

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

}
