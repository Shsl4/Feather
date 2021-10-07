package dev.sl4sh.feather.callbacks;

import dev.sl4sh.feather.events.PlayerConnectingEvent;
import dev.sl4sh.feather.listener.FeatherCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerConnectingCallback extends FeatherCallback {

    Event<PlayerConnectingCallback> EVENT = EventFactory.createArrayBacked(PlayerConnectingCallback.class, (listeners) -> (event) -> {

        for (PlayerConnectingCallback listener : listeners){

            listener.onPlayerConnect(event);

        }

    });

    void onPlayerConnect(PlayerConnectingEvent event);

}
