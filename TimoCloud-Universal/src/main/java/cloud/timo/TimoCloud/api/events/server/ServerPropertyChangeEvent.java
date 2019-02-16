package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PropertyChangeEvent;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class ServerPropertyChangeEvent<T> extends PropertyChangeEvent<ServerObject, T> {

    public ServerPropertyChangeEvent(ServerObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    public ServerPropertyChangeEvent(String instanceId, T oldValue, T newValue) {
        super(instanceId, oldValue, newValue);
    }

    @Override
    public ServerObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getServer(getInstanceId());
    }

    public ServerObject getServer() {
        return getInstance();
    }
}
