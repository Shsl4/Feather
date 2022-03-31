package dev.sl4sh.feather;

import com.google.gson.Gson;
import dev.sl4sh.feather.client.rendering.RenderHandler;
import dev.sl4sh.feather.commands.CommandProcessor;
import dev.sl4sh.feather.event.CommandRegistrationEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.items.debug.LineDrawer;
import dev.sl4sh.feather.items.tools.HammerTool;
import dev.sl4sh.feather.server.FeatherServer;
import dev.sl4sh.feather.services.BackService;
import dev.sl4sh.feather.services.DatabaseService;
import dev.sl4sh.feather.services.PermissionService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Feather implements ModInitializer {

    public static final String MOD_ID = "feather";

    private static final Logger LOGGER = LogManager.getLogger("Feather");
    private static final Gson GSON = new Gson();
    private static final EventRegistry EVENT_REGISTRY = new EventRegistry();
    private static final DatabaseService DATABASE_SERVICE = new DatabaseService();
    private static final PermissionService PERMISSION_SERVICE = new PermissionService();
    private static final BackService BACK_SERVICE = new BackService(EVENT_REGISTRY);

    public static final HammerTool HAMMER_TOOL = new HammerTool();
    public static final LineDrawer LINE_DRAWER = new LineDrawer();

    public static Logger getLogger() { return LOGGER; }
    public static EventRegistry getEventRegistry(){ return EVENT_REGISTRY; }
    public static DatabaseService getDatabaseService(){ return DATABASE_SERVICE; }
    public static PermissionService getPermissionService(){ return PERMISSION_SERVICE; }
    public static BackService getBackService(){ return BACK_SERVICE; }
    public static Gson getGson(){ return GSON; }

    public static final ItemGroup FEATHER_ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier("feather", "general"),
            () -> new ItemStack(Items.NETHER_STAR));

    @Override
    public void onInitialize() {

        getLogger().info("Initializing Feather...");

        Registry.register(Registry.ITEM, new Identifier("feather", "hammer"), HAMMER_TOOL);
        Registry.register(Registry.ITEM, new Identifier("feather", "line_drawer"), LINE_DRAWER);

    }



}