package dev.sl4sh.feather;

import dev.sl4sh.feather.listener.FeatherCallback;
import dev.sl4sh.feather.listener.FeatherEvent;
import dev.sl4sh.feather.listener.Listener;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class EventManager {

    private static boolean initialized = false;
    private static final Map<Class<? extends FeatherEvent>, Event<FeatherCallback>> callbacks = new HashMap<>();

    public static Map<Class<? extends FeatherEvent>, Event<FeatherCallback>> getCallbacks(){
        return callbacks;
    }

    public static void registerEvent(Class<? extends FeatherEvent> forClass, Method method) {

        getOrCreateEvent(forClass).register((event) -> {

            try {
                method.invoke(null, event);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

        Feather.getLogger().info("Registered " + method.getName() + " listener.");

    }

    public static Event<FeatherCallback> getOrCreateEvent(Class<? extends FeatherEvent> eventClass){

        if(getCallbacks().containsKey(eventClass)){

            return getCallbacks().get(eventClass);

        }

        Event<FeatherCallback> callback = EventFactory.createArrayBacked(FeatherCallback.class, (listeners) -> (event) -> {

            for (FeatherCallback listener : listeners){

                listener.execute(event);

            }

        });

        callbacks.put(eventClass, callback);
        return callback;
    }

    public static void init(){

        if(initialized){
            throw new IllegalStateException("EventManager should not be initialized more that once.");
        }

        List<Method> methods = getListenerMethods();

        for (Method method : methods){

            Class<?>[] paramTypes = method.getParameterTypes();

            if(paramTypes.length == 0){

                Feather.getLogger().error("The listener method " + method.getName() + " has no arguments.");
                continue;

            }

            if(paramTypes.length > 1){

                Feather.getLogger().error("The listener method " + method.getName() +
                        " has too much parameter (expected 1, got " + paramTypes.length + ".");

                continue;

            }

            Class<?> eventClass = method.getParameterTypes()[0];

            if(!FeatherEvent.class.isAssignableFrom(eventClass)) {

                Feather.getLogger().error("The listener method " + method.getName() +
                        " does not have an event as it's first parameter.");
                continue;

            }

            Class<? extends FeatherEvent> evClass = (Class<? extends FeatherEvent>)method.getParameterTypes()[0];

            try {
                registerEvent(evClass, method);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }

        initialized = true;

    }

    private static Set<Class<?>> getAllClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    private static List<Method> getListenerMethods(){

        List<Method> methods = new ArrayList<>();

        Set<Class<?>> classes = getAllClasses("dev.sl4sh.feather");

        for (Class<?> cl : classes){

            Method[] mts = cl.getMethods();

            for (Method method : mts){

                if(method.isAnnotationPresent(Listener.class)){

                    methods.add(method);

                }

            }

        }

        return methods;
    }

}
