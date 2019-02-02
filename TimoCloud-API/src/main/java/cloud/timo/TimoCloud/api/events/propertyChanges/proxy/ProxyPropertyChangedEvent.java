package cloud.timo.TimoCloud.api.events.propertyChanges.proxy;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.propertyChanges.PropertyChangedEvent;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public abstract class ProxyPropertyChangedEvent<T> extends PropertyChangedEvent<ProxyObject, T> {

    public ProxyPropertyChangedEvent(ProxyObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public ProxyObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getProxy(getInstanceId());
    }
}
