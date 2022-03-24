package dev.sl4sh.feather;

import dev.sl4sh.feather.db.DatabaseManager;
import dev.sl4sh.feather.event.CommandRegistrationEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.event.registration.EventResponder;
import dev.sl4sh.feather.event.registration.Register;
import dev.sl4sh.feather.items.debug.LineDrawer;
import dev.sl4sh.feather.items.tools.HammerTool;
import dev.sl4sh.feather.permissions.PermissionManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

@EventResponder
public class Feather implements ModInitializer {

    public static final String MOD_ID = "feather";

    private static final Logger LOGGER = LogManager.getLogger("Feather");
    private static final EventRegistry EVENT_REGISTRY = new EventRegistry();
    private static final DatabaseManager DATABASE_MANAGER = new DatabaseManager();
    private static final PermissionManager PERMISSION_MANAGER = new PermissionManager();

    public static final HammerTool HAMMER_TOOL = new HammerTool();
    public static final LineDrawer LINE_DRAWER = new LineDrawer();

    public static Logger getLogger() { return LOGGER; }
    public static EventRegistry getEventRegistry(){ return EVENT_REGISTRY;}
    public static DatabaseManager getDatabaseManager(){ return DATABASE_MANAGER; }
    public static PermissionManager getPermissionManager(){ return PERMISSION_MANAGER; }

    public static final ItemGroup FEATHER_ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier("feather", "general"),
            () -> new ItemStack(Items.NETHER_STAR));

    @Register
    public static void register(EventRegistry registry){

        registry.COMMAND_REGISTRATION.register(Feather::registerCommands);

    }

    public static void registerCommands(CommandRegistrationEvent event){

        event.register(CommandManager.literal("cool").executes((context) -> {

            for(var a : getPermissionManager().getPermissions()){

                getLogger().info(a.getId());

            }

            return 0;

        }));


    }

    @Override
    public void onInitialize() {

        getLogger().info("Initializing Feather...");

        for(var m : FabricLoader.getInstance().getAllMods()){

            getLogger().info(m.getMetadata().getId());

        }

        Registry.register(Registry.ITEM, new Identifier("feather", "hammer"), HAMMER_TOOL);
        Registry.register(Registry.ITEM, new Identifier("feather", "line_drawer"), LINE_DRAWER);

    }



}