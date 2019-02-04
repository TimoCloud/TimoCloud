package cloud.timo.TimoCloud.api.events.propertyChanges.serverGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public class ServerGroupBaseChangedEvent extends ServerGroupPropertyChangedEvent<BaseObject> {

    private String oldBaseId;
    private String newBaseId;

    public ServerGroupBaseChangedEvent(ServerGroupObject instance, BaseObject oldValue, BaseObject newValue) {
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
        return EventType.SG_BASE_CHANGED;
    }
}
