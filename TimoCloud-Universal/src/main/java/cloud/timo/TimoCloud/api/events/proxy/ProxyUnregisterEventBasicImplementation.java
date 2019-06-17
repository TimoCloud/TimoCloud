package cloud.timo.TimoCloud.api.events.proxy;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class ProxyUnregisterEventBasicImplementation implements ProxyUnregisterEvent {

    private ProxyObject proxy;

    public ProxyUnregisterEventBasicImplementation() {
    }

    public ProxyUnregisterEventBasicImplementation(ProxyObject proxy) {
        this.proxy = proxy;
    }

    public ProxyObject getProxy() {
        return proxy;
    }

    @Override
    public EventType getType() {
        return EventType.PROXY_UNREGISTER;
    }
}
