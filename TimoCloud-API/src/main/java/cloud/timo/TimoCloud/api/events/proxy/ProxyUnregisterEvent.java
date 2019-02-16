package cloud.timo.TimoCloud.api.events.proxy;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public interface ProxyUnregisterEvent extends Event {

    ProxyObject getProxy();

}
