package cloud.timo.TimoCloud.api.events.proxy;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class ProxyOnlinePlayerCountChangeEventBasicImplementation extends ProxyPropertyChangeEvent<Integer> implements ProxyOnlinePlayerCountChangeEvent {
    private ProxyObject proxy;

    public ProxyOnlinePlayerCountChangeEventBasicImplementation() {

    }

    public ProxyOnlinePlayerCountChangeEventBasicImplementation(ProxyObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
        this.proxy = instance;
    }

    @Override
    public ProxyObject getProxy() {
        return proxy;
    }

    @Override
    public EventType getType() {
        return EventType.P_ONLINE_PLAYER_COUNT_CHANGE;
    }

}
