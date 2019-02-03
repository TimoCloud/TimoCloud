package cloud.timo.TimoCloud.api.events.propertyChanges.proxy;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class ProxyPortChangedEvent extends ProxyPropertyChangedEvent<Integer> {

    public ProxyPortChangedEvent(ProxyObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.P_PORT_CHANGED;
    }
}
