package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public interface ProxyGroupStaticChangeEvent extends Event {

    ProxyGroupObject getProxyGroup();

    Boolean getOldValue();

    Boolean getNewValue();
}
