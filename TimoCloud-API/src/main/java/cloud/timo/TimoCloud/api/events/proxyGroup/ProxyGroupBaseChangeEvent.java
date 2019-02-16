package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public interface ProxyGroupBaseChangeEvent extends Event {

    ProxyGroupObject getProxyGroup();

    BaseObject getOldValue();

    BaseObject getNewValue();

}
