package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public interface ProxyGroupMotdChangeEvent extends Event {

    ProxyGroupObject getProxyGroup();

    String getOldValue();

    String getNewValue();

}
