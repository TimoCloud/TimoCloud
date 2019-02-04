package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.propertyChanges.PropertyChangedEvent;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public abstract class ProxyGroupPropertyChangedEvent<T> extends PropertyChangedEvent<ProxyGroupObject, T> {

    public ProxyGroupPropertyChangedEvent(ProxyGroupObject instannce) {
        super(instannce);
    }

    public ProxyGroupPropertyChangedEvent(ProxyGroupObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public ProxyGroupObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getProxyGroup(getInstanceId());
    }
}
