package dev.sl4sh.feather.event.registration;

import dev.sl4sh.feather.Service;
import dev.sl4sh.feather.event.*;
import dev.sl4sh.feather.event.client.ClientDisconnectEvent;
import dev.sl4sh.feather.event.player.*;
import dev.sl4sh.feather.event.world.WorldDebugRenderEvent;

public class EventRegistry implements Service {

    private static boolean initialized = false;

    public final EventWrapper<PlayerPreConnectEvent> PRE_CONNECT = new EventWrapper<>();
    public final EventWrapper<PlayerPostConnectEvent> POST_CONNECT = new EventWrapper<>();
    public final EventWrapper<PlayerPreDimensionChangeEvent> PRE_DIM_CHANGE = new EventWrapper<>();
    public final EventWrapper<PlayerPostDimensionChangeEvent> POST_DIM_CHANGE = new EventWrapper<>();
    public final EventWrapper<PlayerPreDisconnectEvent> PRE_DISCONNECT = new EventWrapper<>();
    public final EventWrapper<PlayerPostDisconnectEvent> POST_DISCONNECT = new EventWrapper<>();
    public final EventWrapper<PlayerPreRespawnEvent> PRE_RESPAWN = new EventWrapper<>();
    public final EventWrapper<PlayerPostRespawnEvent> POST_RESPAWN = new EventWrapper<>();
    public final EventWrapper<PlayerPreTeleportEvent> PRE_TELEPORT = new EventWrapper<>();
    public final EventWrapper<PlayerPostTeleportEvent> POST_TELEPORT = new EventWrapper<>();
    public final EventWrapper<PlayerPostDeathEvent> POST_DEATH = new EventWrapper<>();
    public final EventWrapper<WorldDebugRenderEvent> DEBUG_RENDER = new EventWrapper<>();
    public final EventWrapper<ClientDisconnectEvent> CLIENT_SIDE_DISCONNECT = new EventWrapper<>();
    public final EventWrapper<CommandRegistrationEvent> COMMAND_REGISTRATION = new EventWrapper<>();
    public final EventWrapper<CommandExecutionEvent> COMMAND_EXECUTION = new EventWrapper<>();

    public EventRegistry() {

        if(initialized){
            throw new IllegalStateException("There should not be more than one instance of EventRegistry.");
        }

        initialized = true;

    }

    @Override
    public void loadConfiguration() {

    }

    @Override
    public void writeConfiguration() {

    }

    @Override
    public boolean getServiceState() {
        return false;
    }
}
