package dev.sl4sh.feather.util;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;

public class Utilities {

    public static String getWorldDimensionName(ServerWorld world){
        return ((ServerWorldProperties)(world.getLevelProperties())).getLevelName();
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

}
