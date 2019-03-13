package cloud.timo.TimoCloud.api.implementations.managers;

import cloud.timo.TimoCloud.api.TimoCloudEventAPI;
import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

public class EventManager implements TimoCloudEventAPI {

    private Collection<Listener> listeners;

    public EventManager() {
        listeners = new LinkedHashSet<>();
    }

    @Override
    public void registerListener(Listener listener) {
        if (getListeners().contains(listener)) return;
        getListeners().add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        if (!getListeners().contains(listener)) return;
        getListeners().remove(listener);
    }

    public void callEvent(Event event) {
        fireEvent(event);
    }

    private void fireEvent(Event event) {
        for (Listener listener : getListeners()) {
            Class c = listener.getClass();
            final Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                try {
                    EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                    if (eventHandler == null) continue;
                    if (method.getParameterTypes().length != 1 || ! method.getParameterTypes()[0].equals(event.getClass())) continue;
                    method.invoke(listener, event);
                } catch (Exception e) {
                    System.err.println("Uncaught exception while firing event: ");
                    e.printStackTrace();
                }
            }
        }
    }

    public Collection<Listener> getListeners() {
        return listeners;
    }
}
