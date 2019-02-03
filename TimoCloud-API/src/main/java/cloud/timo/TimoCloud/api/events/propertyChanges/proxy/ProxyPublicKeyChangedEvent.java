package cloud.timo.TimoCloud.api.events.propertyChanges.proxy;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

import java.security.PublicKey;

public class ProxyPublicKeyChangedEvent extends ProxyPropertyChangedEvent<PublicKey> {

    public ProxyPublicKeyChangedEvent(ProxyObject instance, PublicKey oldValue, PublicKey newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.P_PUBLIC_KEY_CHANGED;
    }
}
