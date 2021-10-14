package dev.sl4sh.feather;

import dev.sl4sh.feather.event.player.PlayerPreConnectEvent;
import dev.sl4sh.feather.event.player.PlayerPreDimensionChangeEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.event.registration.EventResponder;
import dev.sl4sh.feather.event.registration.Register;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.text.Text;

@EventResponder
public class ListenerTest {

    @Register
    public static void register(EventRegistry registry) {

        registry.PRE_CONNECT.register(ListenerTest::onJoin);
        registry.PRE_DIM_CHANGE.register(ListenerTest::onDimensionChange);

    }

    private static void onDimensionChange(PlayerPreDimensionChangeEvent event){

        event.setCancelled(true, "U suck");
        event.getPlayer().sendMessage(Text.of("\u00a7cYou may not travel to " + Utilities.getNiceWorldDimensionName(event.getDestination())), false);

    }

    private static void onJoin(PlayerPreConnectEvent a){

        Feather.getLogger().info(a.getPlayer().getName().getString());
    }

}
