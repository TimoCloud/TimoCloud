package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupCreatedEventBasicImplementation implements ProxyGroupCreatedEvent {

    private ProxyGroupObject proxyGroupObject;

    public ProxyGroupCreatedEventBasicImplementation(ProxyGroupObject proxyGroupObject) {
        this.proxyGroupObject = proxyGroupObject;
    }

    @Override
    public ProxyGroupObject getProxyGroup() {
        return proxyGroupObject;
    }

    @Override
    public EventType getType() {
        return EventType.PG_CRAETED;
    }
}
