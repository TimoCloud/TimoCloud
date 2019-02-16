package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupBaseChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<BaseObject> implements ProxyGroupBaseChangeEvent {

    private String oldBaseId;
    private String newBaseId;

    public ProxyGroupBaseChangeEventBasicImplementation(ProxyGroupObject instance, BaseObject oldValue, BaseObject newValue) {
        super(instance);
        oldBaseId = oldValue == null ? null : oldValue.getId();
        newBaseId = newValue == null ? null : newValue.getId();
    }

    public ProxyGroupBaseChangeEventBasicImplementation(String instanceId, String oldBaseId, String newBaseId) {
        super(instanceId);
        this.oldBaseId = oldBaseId;
        this.newBaseId = newBaseId;
    }

    @Override
    public BaseObject getOldValue() {
        return oldBaseId == null ? null : TimoCloudAPI.getUniversalAPI().getBase(oldBaseId);
    }

    @Override
    public BaseObject getNewValue() {
        return newBaseId == null ? null : TimoCloudAPI.getUniversalAPI().getBase(newBaseId);
    }

    @Override
    public EventType getType() {
        return EventType.PG_BASE_CHANGE;
    }
}
