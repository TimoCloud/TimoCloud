package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupProxyChooseStrategyChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<ProxyChooseStrategy> implements ProxyGroupProxyChooseStrategyChangeEvent {

    public ProxyGroupProxyChooseStrategyChangeEventBasicImplementation(ProxyGroupObject instance, ProxyChooseStrategy oldValue, ProxyChooseStrategy newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_PROXY_CHOOSE_STRATEGY_CHANGE;
    }

}
