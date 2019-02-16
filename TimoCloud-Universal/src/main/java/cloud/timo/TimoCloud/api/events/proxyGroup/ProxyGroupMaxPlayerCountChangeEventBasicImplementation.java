package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupMaxPlayerCountChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<Integer> implements ProxyGroupMaxPlayerCountChangeEvent {

    public ProxyGroupMaxPlayerCountChangeEventBasicImplementation(ProxyGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_MAX_PLAYER_COUNT_CHANGE;
    }
}
