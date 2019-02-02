package cloud.timo.TimoCloud.api.events.propertyChanges;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.IdentifiableObject;

public abstract class PropertyChangedEvent<I extends IdentifiableObject, T> implements Event {

    private String instanceId;
    private T oldValue;
    private T newValue;

    public PropertyChangedEvent(I instance, T oldValue, T newValue) {
        this.instanceId = instance.getId();
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    protected String getInstanceId() {
        return instanceId;
    }

    public abstract I getInstance();

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
