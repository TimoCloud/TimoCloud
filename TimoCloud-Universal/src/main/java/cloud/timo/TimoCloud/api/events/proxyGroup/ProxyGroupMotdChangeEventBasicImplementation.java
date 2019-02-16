package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupMotdChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<String> implements ProxyGroupMotdChangeEvent {

    public ProxyGroupMotdChangeEventBasicImplementation(ProxyGroupObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_MOTD_CHANGE;
    }
}
