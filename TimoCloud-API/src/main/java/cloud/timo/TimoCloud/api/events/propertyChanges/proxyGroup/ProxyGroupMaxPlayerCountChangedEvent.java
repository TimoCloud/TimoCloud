package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupMaxPlayerCountChangedEvent  extends ProxyGroupPropertyChangedEvent<Integer> {

    public ProxyGroupMaxPlayerCountChangedEvent(ProxyGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_MAX_PLAYER_COUNT_CHANGED;
    }
}
