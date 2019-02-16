package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public interface ProxyGroupKeepFreeSlotsChangeEvent extends Event {

    ProxyGroupObject getProxyGroup();

    Integer getOldValue();

    Integer getNewValue();

}
