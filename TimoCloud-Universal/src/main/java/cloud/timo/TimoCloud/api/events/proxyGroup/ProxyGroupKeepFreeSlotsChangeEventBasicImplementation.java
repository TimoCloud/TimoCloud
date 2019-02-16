package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupKeepFreeSlotsChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<Integer> implements ProxyGroupKeepFreeSlotsChangeEvent {

    public ProxyGroupKeepFreeSlotsChangeEventBasicImplementation(ProxyGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_KEEP_FREE_SLOTS_CHANGE;
    }
}
