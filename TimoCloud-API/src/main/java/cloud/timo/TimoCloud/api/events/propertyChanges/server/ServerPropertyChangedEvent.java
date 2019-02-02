package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.propertyChanges.PropertyChangedEvent;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public abstract class ServerPropertyChangedEvent<T> extends PropertyChangedEvent<ServerObject, T> {

    public ServerPropertyChangedEvent(ServerObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public ServerObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getServer(getInstanceId());
    }
}
