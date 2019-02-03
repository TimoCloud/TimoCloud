package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

import java.net.InetAddress;

public class BasePublicAddressChangedEvent extends BasePropertyChangedEvent<InetAddress> {

    public BasePublicAddressChangedEvent(BaseObject instance, InetAddress oldValue, InetAddress newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_PUBLIC_ADDRESS_CHANGED;
    }
}
