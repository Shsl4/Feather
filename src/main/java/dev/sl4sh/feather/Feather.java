package dev.sl4sh.feather;

import dev.sl4sh.feather.events.*;
import dev.sl4sh.feather.items.debug.LineDrawer;
import dev.sl4sh.feather.items.tools.HammerTool;
import dev.sl4sh.feather.client.linerenderer.LineRenderer;
import dev.sl4sh.feather.listener.Listener;
import dev.sl4sh.feather.util.Utilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

//@Environment(EnvType.SERVER)
public class Feather implements ModInitializer {

    public static Logger getLogger() { return LOGGER; }

    private static final Logger LOGGER = LogManager.getLogger("Feather");

    public static final HammerTool HAMMER_TOOL = new HammerTool();
    public static final LineDrawer LINE_DRAWER = new LineDrawer();

    public static final ItemGroup FEATHER_ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier("feather", "general"),
            () -> new ItemStack(Items.NETHER_STAR));

    @Listener
    public static void onTeleport(PlayerPreTeleportEvent event){

        event.setCancelled(true, "Because I want to");
        event.getPlayer().sendSystemMessage(new LiteralText("\u00A7cYou are not allowed to teleport."), UUID.randomUUID());

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

    public static Vec3f toVec3f(Vec3i vec){
        return new Vec3f(vec.getX(), vec.getY(), vec.getZ());
    }
    public static Vec3f toVec3f(Vec3d vec){
        return new Vec3f((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());
    }

    @Override
    public void onInitialize() {

        getLogger().info("Initializing Feather...");

        EventManager.init();

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            Vec3d pos = ctx.camera().getPos();

            if(ctx.consumers() != null) {

                LineRenderer.INSTANCE.render(ctx.matrixStack(), ctx.consumers(), pos.x, pos.y, pos.z);

            }

        });

        Registry.register(Registry.ITEM, new Identifier("feather", "hammer"), HAMMER_TOOL);
        Registry.register(Registry.ITEM, new Identifier("feather", "line_drawer"), LINE_DRAWER);

    }



}