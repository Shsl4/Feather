package dev.sl4sh.feather;

import dev.sl4sh.feather.callbacks.PlayerConnectedCallback;
import dev.sl4sh.feather.callbacks.PlayerConnectingCallback;
import dev.sl4sh.feather.events.PlayerConnectedEvent;
import dev.sl4sh.feather.events.PlayerConnectingEvent;
import dev.sl4sh.feather.listener.FeatherEvent;
import dev.sl4sh.feather.listener.Listener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.LambdaMetafactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class EventManager {

    private static boolean initiated = false;

    private static void registerEvent(Class<?> from, Method method){

        if (from.equals(PlayerConnectingEvent.class)){

            PlayerConnectingCallback.EVENT.register((event -> {

                try {

                    method.invoke(null, event);

                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }

            }));

        }
        else if(from.equals(PlayerConnectedEvent.class)){

            PlayerConnectedCallback.EVENT.register((event -> {

                try {
                    method.invoke(null, event);

                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }

            }));

        }
        else{

            Feather.getLogger().error("Unknown callback received: " + from.getName());
            return;

        }

        Feather.getLogger().info("Registered " + method.getName() + " listener.");

    }

    public static void init(){

        if(initiated){
            throw new IllegalStateException("EventManager should not be initiated more that once.");
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

            registerEvent(eventClass, method);

        }

        initiated = true;

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

        List<Method> methods = new ArrayList<Method>();

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
