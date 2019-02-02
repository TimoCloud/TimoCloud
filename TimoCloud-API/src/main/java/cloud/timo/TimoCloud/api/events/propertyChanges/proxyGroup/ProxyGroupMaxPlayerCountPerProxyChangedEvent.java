package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupMaxPlayerCountPerProxyChangedEvent  extends ProxyGroupPropertyChangedEvent<Integer> {

    public ProxyGroupMaxPlayerCountPerProxyChangedEvent(ProxyGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_MAX_PLAYER_COUNT_PER_PROXY_CHANGED;
    }
}
