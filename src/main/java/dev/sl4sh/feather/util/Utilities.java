package dev.sl4sh.feather.util;

import dev.sl4sh.feather.Permission;
import net.minecraft.client.util.Session;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

public class Utilities {

    public static boolean createWorld(MinecraftServer server){

        return false;

    }

    public static Writer makeWriter(String path) throws IOException {

        File groupsFile = new File(path);
        groupsFile.getParentFile().mkdirs();
        groupsFile.createNewFile();
        return new FileWriter(groupsFile);

    }

    public static Optional<ServerWorld> getWorldByName(MinecraftServer server, String name){

        for (ServerWorld world : server.getWorlds()){
            if (getWorldDimensionName(world).equals(name)){
                return Optional.of(world);
            }
        }

        return Optional.empty();

    }

    public static String getWorldDimensionName(ServerWorld world){
        return world.getRegistryKey().getValue().getPath();
    }

    public static String getNiceWorldDimensionName(ServerWorld world){
        String dimName = getWorldDimensionName(world);
        return switch (dimName) {
            case "the_end" -> "The End";
            case "the_nether" -> "The Nether";
            case "overworld" -> "The Overworld";
            default -> dimName;
        };

    }

    public static Vec3f toVec3f(Vec3i vec){
        return new Vec3f(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec3f toVec3f(Vec3d vec){
        return new Vec3f((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object object){
        return (T) object;
    }

    public static void sendSuccess(ServerCommandSource source, String message){

        source.sendFeedback(Text.of("\u00a7a" + message), false);

    }

    public static void sendError(ServerCommandSource source, String message){

        source.sendError(Text.of(message));

    }

    public static void sendWarning(ServerCommandSource source, String message){

        source.sendFeedback(Text.of("\u00a76" + message), false);

    }

    public static void restoreHealth(ServerPlayerEntity player){
        player.setHealth(player.getMaxHealth());
    }

    public static void feed(ServerPlayerEntity player){

        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setExhaustion(0);
        player.getHungerManager().setSaturationLevel(20.0f);

    }

    public static void resendCommandTrees(ServerCommandSource source, Permission.Group group) {

        for (UUID uuid : group.getUsers().keySet()){

            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(uuid);

            if (player != null){
                source.getServer().getCommandManager().sendCommandTree(player);
            }

        }

    }

}
