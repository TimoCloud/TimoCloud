package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PropertyChangeEvent;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class ServerGroupPropertyChangeEvent<T> extends PropertyChangeEvent<ServerGroupObject, T> {

    public ServerGroupPropertyChangeEvent(ServerGroupObject instance) {
        super(instance);
    }

    public ServerGroupPropertyChangeEvent(String instanceId) {
        super(instanceId);
    }

    public ServerGroupPropertyChangeEvent(ServerGroupObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    public ServerGroupPropertyChangeEvent(String instanceId, T oldValue, T newValue) {
        super(instanceId, oldValue, newValue);
    }

    @Override
    public ServerGroupObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getServerGroup(getInstanceId());
    }

    public ServerGroupObject getServerGroup() {
        return getInstance();
    }
}
