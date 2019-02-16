package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PropertyChangeEvent;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class ProxyGroupPropertyChangeEvent<T> extends PropertyChangeEvent<ProxyGroupObject, T> {

    public ProxyGroupPropertyChangeEvent(ProxyGroupObject instance) {
        super(instance);
    }

    public ProxyGroupPropertyChangeEvent(String instanceId) {
        super(instanceId);
    }

    public ProxyGroupPropertyChangeEvent(ProxyGroupObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    public ProxyGroupPropertyChangeEvent(String instanceId, T oldValue, T newValue) {
        super(instanceId, oldValue, newValue);
    }

    @Override
    public ProxyGroupObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getProxyGroup(getInstanceId());
    }

    public ProxyGroupObject getProxyGroup() {
        return getInstance();
    }
}
