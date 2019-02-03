package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.net.InetSocketAddress;

public class ServerAddressChangedEvent extends ServerPropertyChangedEvent<InetSocketAddress> {

    public ServerAddressChangedEvent(ServerObject instance, InetSocketAddress oldValue, InetSocketAddress newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_ADDRESS_CHANGED;
    }
}
