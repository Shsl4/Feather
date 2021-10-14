package dev.sl4sh.feather.event.registration;

import dev.sl4sh.feather.event.*;
import dev.sl4sh.feather.event.client.ClientDisconnectEvent;
import dev.sl4sh.feather.event.player.*;
import dev.sl4sh.feather.event.world.WorldDebugRenderEvent;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

public class EventRegistry {

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
            throw new IllegalStateException("There should not be more than one instance of EventManager.");
        }

        Reflections reflections = new Reflections("dev.sl4sh.feather");
        Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(EventResponder.class);

        for (Class<?> cl : subTypes){

            for (Method m : cl.getMethods()){

                if(m.isAnnotationPresent(Register.class)){

                    assert Modifier.isStatic(m.getModifiers()) &&
                            Modifier.isPublic(m.getModifiers()) &&
                            m.getReturnType() == Void.class &&
                            m.getParameterTypes().length == 1 &&
                            m.getParameterTypes()[0].getSuperclass() == FeatherEvent.class;

                    try {
                        m.invoke(null, this);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        assert false;
                    }

                }

            }

        }

        initialized = true;

    }

}
