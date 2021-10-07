package dev.sl4sh.feather.util;

import net.minecraft.server.world.ServerWorld;

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

    @SuppressWarnings("unchecked")
    public static <T> T as(Object object){
        return (T) object;
    }

}
