package dev.sl4sh.feather.event.client;

import dev.sl4sh.feather.event.FeatherEvent;
import net.minecraft.client.gui.screen.Screen;

public class ClientDisconnectEvent implements FeatherEvent {

    private final Screen screen;

    public ClientDisconnectEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

}
