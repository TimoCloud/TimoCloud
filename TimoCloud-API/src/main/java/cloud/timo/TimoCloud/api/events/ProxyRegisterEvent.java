package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class ProxyRegisterEvent implements Event {

    private ProxyObject proxy;

    public ProxyRegisterEvent() {
    }

    public ProxyRegisterEvent(ProxyObject proxy) {
        this.proxy = proxy;
    }

    public ProxyObject getProxy() {
        return proxy;
    }

    @Override
    public EventType getType() {
        return EventType.PROXY_REGISTER;
    }
}
