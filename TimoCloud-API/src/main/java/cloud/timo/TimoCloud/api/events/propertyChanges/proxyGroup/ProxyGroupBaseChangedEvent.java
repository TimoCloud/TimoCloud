package cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupBaseChangedEvent extends ProxyGroupPropertyChangedEvent<BaseObject> {

    private String oldBaseId;
    private String newBaseId;

    public ProxyGroupBaseChangedEvent(ProxyGroupObject instance, BaseObject oldValue, BaseObject newValue) {
        super(instance);
        oldBaseId = oldValue.getId();
        newBaseId = newValue.getId();
    }

    @Override
    public BaseObject getOldValue() {
        return TimoCloudAPI.getUniversalAPI().getBase(oldBaseId);
    }

    @Override
    public BaseObject getNewValue() {
        return TimoCloudAPI.getUniversalAPI().getBase(newBaseId);
    }

    @Override
    public EventType getType() {
        return EventType.PG_BASE_CHANGED;
    }
}
