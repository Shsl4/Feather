package dev.sl4sh.feather;

import dev.sl4sh.feather.events.*;
import dev.sl4sh.feather.listener.Listener;
import dev.sl4sh.feather.util.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;

@Environment(EnvType.SERVER)
public class Feather implements ModInitializer {

    public static Logger getLogger() { return LOGGER; }

    private static final Logger LOGGER = LogManager.getLogger("Feather");

    @Listener
    public static void onTeleport(PlayerPreTeleportEvent event){

        Vec3d pos = event.getPosition();
        getLogger().info("Teleported {} at {}, {}, {}", event.getPlayer().getName().getString(), pos.x, pos.y, pos.z);

    }

    @Listener
    public static void onPlayerConnecting(PlayerPreConnectEvent event){
        getLogger().info(event.getPlayer().getName().getString() + " is joining the server...");
    }

    @Listener
    public static void onPlayerConnected(PlayerPostConnectEvent event){
        getLogger().info(event.getPlayer().getName().getString() + " connected.");
    }


    @Listener
    public static void onDeath(PlayerPostDeathEvent event){

        Entity killer = event.getDamageSource().getSource();

        if(killer != null){
            getLogger().info((killer.getName().getString() + " killed " + event.getPlayer().getName().getString() + "."));
        }
        else{
            getLogger().info(event.getPlayer().getName().getString() + " died.");
        }

    }


    @Listener
    public static void dimChange(PlayerPreDimensionChangeEvent event){

        event.setCancelled(true, "Because I want to");
        String niceName = Utilities.getNiceWorldDimensionName(event.getDestination());
        getLogger().info("Prevented " +
                event.getPlayer().getName().getString() +
                " from changing dimension to " +
                niceName);
        event.getPlayer().sendSystemMessage(new LiteralText("\u00A7cYou are not allowed to travel to " + niceName + "."), UUID.randomUUID());

    }

    @Listener
    public static void onPlayerPreDisconnect(PlayerPreDisconnectEvent event){
        getLogger().info(event.getPlayer().getName().getString() + " is disconnecting...");
    }

    @Listener
    public static void onPlayerPostDisconnect(PlayerPostDisconnectEvent event){
        getLogger().info(event.getPlayer().getName().getString() + " disconnected.");
    }

    @Listener
    public static void onPreRespawn(PlayerPreRespawnEvent event){
        getLogger().info("World before respawn " + Utilities.getWorldDimensionName(event.getWorld()));
    }

    @Listener
    public static void onPostRespawn(PlayerPostRespawnEvent event){
        getLogger().info("World after respawn " + Utilities.getWorldDimensionName(event.getWorld()));
    }

    @Override
    public void onInitialize() {

        getLogger().info("Initializing Feather...");

        EventManager.init();

    }


}