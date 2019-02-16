package cloud.timo.TimoCloud.api.events.proxy;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class ProxyRegisterEventBasicImplementation implements ProxyRegisterEvent {

    private ProxyObject proxy;

    public ProxyRegisterEventBasicImplementation() {
    }

    public ProxyRegisterEventBasicImplementation(ProxyObject proxy) {
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
