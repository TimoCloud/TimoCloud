package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupBaseNameChangedEvent  extends ProxyGroupPropertyChangedEvent<String> {

    public ProxyGroupBaseNameChangedEvent(ProxyGroupObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_BASE_NAME_CHANGED;
    }
}
