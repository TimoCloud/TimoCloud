package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.propertyChanges.PropertyChangedEvent;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public abstract class BasePropertyChangedEvent<T> extends PropertyChangedEvent<BaseObject, T> {

    public BasePropertyChangedEvent(BaseObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public BaseObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getBase(getInstanceId());
    }
}
