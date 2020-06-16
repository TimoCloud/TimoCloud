package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

import java.util.List;

public interface ProxyGroupJavaParametersChangeEvent extends Event {

    ProxyGroupObject getProxyGroup();

    List<String> getOldValue();

    List<String> getNewValue();

}
