package cloud.timo.TimoCloud.api.events.propertyChanges.proxy;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

import java.net.InetSocketAddress;

public class ProxyAddressChangedEvent extends ProxyPropertyChangedEvent<InetSocketAddress> {

    public ProxyAddressChangedEvent(ProxyObject instance, InetSocketAddress oldValue, InetSocketAddress newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.P_ADDRESS_CHANGED;
    }
}
