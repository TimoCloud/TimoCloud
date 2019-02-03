package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.security.PublicKey;

public class ServerPublicKeyChangedEvent extends ServerPropertyChangedEvent<PublicKey> {

    public ServerPublicKeyChangedEvent(ServerObject instance, PublicKey oldValue, PublicKey newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_PUBLIC_KEY_CHANGED;
    }
}
