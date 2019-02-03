package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

import java.net.InetAddress;

public class BaseAddressChangedEvent extends BasePropertyChangedEvent<InetAddress> {

    public BaseAddressChangedEvent(BaseObject instance, InetAddress oldValue, InetAddress newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_ADDRESS_CHANGED;
    }
}
