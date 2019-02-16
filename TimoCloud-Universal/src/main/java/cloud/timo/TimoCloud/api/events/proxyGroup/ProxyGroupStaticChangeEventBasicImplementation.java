package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupStaticChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<Boolean> implements ProxyGroupStaticChangeEvent {

    public ProxyGroupStaticChangeEventBasicImplementation(ProxyGroupObject instance, Boolean oldValue, Boolean newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_STATIC_CHANGE;
    }
}
