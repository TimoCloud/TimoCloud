package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupProxyChooseStrategyChangedEvent  extends ProxyGroupPropertyChangedEvent<Integer> {

    public ProxyGroupProxyChooseStrategyChangedEvent(ProxyGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_PROXY_CHOOSE_STRATEGY_CHANGED;
    }

}
