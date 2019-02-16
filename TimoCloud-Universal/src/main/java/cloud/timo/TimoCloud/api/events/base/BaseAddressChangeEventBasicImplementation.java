package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;

@NoArgsConstructor
public class BaseAddressChangeEventBasicImplementation extends BasePropertyChangeEvent<InetAddress> implements BaseAddressChangeEvent {

    public BaseAddressChangeEventBasicImplementation(BaseObject instance, InetAddress oldValue, InetAddress newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_ADDRESS_CHANGE;
    }
}
