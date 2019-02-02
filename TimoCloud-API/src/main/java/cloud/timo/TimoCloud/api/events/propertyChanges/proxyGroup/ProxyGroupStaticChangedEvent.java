package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupStaticChangedEvent  extends ProxyGroupPropertyChangedEvent<Boolean> {

    public ProxyGroupStaticChangedEvent(ProxyGroupObject instance, Boolean oldValue, Boolean newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_STATIC_CHANGED;
    }
}
