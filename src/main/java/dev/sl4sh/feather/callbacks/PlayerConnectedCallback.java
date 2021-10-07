package dev.sl4sh.feather.callbacks;

import dev.sl4sh.feather.events.PlayerConnectedEvent;
import dev.sl4sh.feather.listener.FeatherCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerConnectedCallback extends FeatherCallback {

    Event<PlayerConnectedCallback> EVENT = EventFactory.createArrayBacked(PlayerConnectedCallback.class, (listeners) -> (event) -> {

        for (PlayerConnectedCallback listener : listeners){

            listener.onPlayerConnect(event);

        }

    });

    void onPlayerConnect(PlayerConnectedEvent event);

}
