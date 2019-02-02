package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupMotdChangedEvent  extends ProxyGroupPropertyChangedEvent<String> {


    public ProxyGroupMotdChangedEvent(ProxyGroupObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_MOTD_CHANGED;
    }
}
