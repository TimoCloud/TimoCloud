package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PropertyChangeEvent;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public abstract class BasePropertyChangeEvent<T> extends PropertyChangeEvent<BaseObject, T> {

    public BasePropertyChangeEvent() {
        super();
    }

    public BasePropertyChangeEvent(BaseObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    public BasePropertyChangeEvent(String instanceId, T oldValue, T newValue) {
        super(instanceId, oldValue, newValue);
    }

    @Override
    public BaseObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getBase(getInstanceId());
    }

    public BaseObject getBase() {
        return getInstance();
    }
}
