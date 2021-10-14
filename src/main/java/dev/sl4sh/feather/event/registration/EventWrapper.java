package dev.sl4sh.feather.event.registration;

import dev.sl4sh.feather.event.FeatherEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventWrapper<T extends FeatherEvent> {

    private final List<Consumer<T>> listeners = new ArrayList<>();

    public final void invoke(T event){

        for (Consumer<T> listener : listeners){

            listener.accept(event);

        }

    }

    public void register(Consumer<T> c){

        if(!listeners.contains(c)){
            listeners.add(c);
        }

    }

}
