package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupMinAmountChangedEvent extends ProxyGroupPropertyChangedEvent<Integer> {

    public ProxyGroupMinAmountChangedEvent(ProxyGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_MIN_AMOUNT_CHANGED;
    }
}
