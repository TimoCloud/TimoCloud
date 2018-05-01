package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class ProxyUnregisterEvent implements Event {

    private ProxyObject proxy;

    public ProxyUnregisterEvent() {
    }

    public ProxyUnregisterEvent(ProxyObject proxy) {
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
