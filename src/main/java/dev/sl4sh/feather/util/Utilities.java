package dev.sl4sh.feather.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

public class Utilities {

    public static String getWorldDimensionName(ServerWorld world){
        return world.getDimension().getSkyProperties().getPath();
    }

    public static String getNiceWorldDimensionName(ServerWorld world){
        String dimName = world.getDimension().getSkyProperties().getPath();
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

}
