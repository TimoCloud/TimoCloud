package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public interface ProxyGroupProxyChooseStrategyChangeEvent extends Event {

    ProxyGroupObject getProxyGroup();

    ProxyChooseStrategy getOldValue();

    ProxyChooseStrategy getNewValue();

}
