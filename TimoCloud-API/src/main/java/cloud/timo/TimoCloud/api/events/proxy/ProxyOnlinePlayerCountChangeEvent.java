package cloud.timo.TimoCloud.api.events.proxy;


import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public interface ProxyOnlinePlayerCountChangeEvent extends Event {

    ProxyObject getProxy();

    Integer getOldValue();

    Integer getNewValue();

}
