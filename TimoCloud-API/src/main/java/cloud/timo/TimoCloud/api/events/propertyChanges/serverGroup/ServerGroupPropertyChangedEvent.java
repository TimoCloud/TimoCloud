package cloud.timo.TimoCloud.api.events.propertyChanges.serverGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.propertyChanges.PropertyChangedEvent;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public abstract class ServerGroupPropertyChangedEvent<T> extends PropertyChangedEvent<ServerGroupObject, T> {

    public ServerGroupPropertyChangedEvent(ServerGroupObject instance) {
        super(instance);
    }

    public ServerGroupPropertyChangedEvent(ServerGroupObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public ServerGroupObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getServerGroup(getInstanceId());
    }
}
