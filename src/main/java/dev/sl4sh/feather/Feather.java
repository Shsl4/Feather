package dev.sl4sh.feather;

import dev.sl4sh.feather.events.PlayerConnectedEvent;
import dev.sl4sh.feather.events.PlayerConnectingEvent;
import dev.sl4sh.feather.listener.Listener;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;


public class Feather implements ModInitializer {

    public static Logger getLogger() { return LOGGER; }

    private static final Logger LOGGER = LogManager.getLogger("Feather");

    @Listener
    public static void onPlayerConnecting(PlayerConnectingEvent event){
        if(new Random().nextBoolean()){
            event.setCancelled(true, "Kicked");
            getLogger().info("Kicking Player...");
        }
    }

    @Listener
    public static void onPlayerConnected(PlayerConnectedEvent event){
        getLogger().info("Player connected!");
    }

    static ActionResult onDeath(ServerPlayerEntity player, DamageSource source){

        if(source.getSource() instanceof CreeperEntity creeper){

            getLogger().info(creeper.getName().getString() + " killed " + player.getName().getString());
        }

        getLogger().info(player.getName().getString() + " died.");
        return ActionResult.PASS;

    }

    static ActionResult onRespawn(ServerPlayerEntity player, boolean alive){

        getLogger().info("Respawned " + player.getName().getString());
        return ActionResult.PASS;

    }

    @Override
    public void onInitialize() {

        getLogger().info("Initializing Feather...");

        EventManager.init();

    }


}