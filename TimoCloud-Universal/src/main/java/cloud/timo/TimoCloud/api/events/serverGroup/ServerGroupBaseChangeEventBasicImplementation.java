package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupBaseChangeEventBasicImplementation extends ServerGroupPropertyChangeEvent<BaseObject> implements ServerGroupBaseChangeEvent {

    private String oldBaseId;
    private String newBaseId;

    public ServerGroupBaseChangeEventBasicImplementation(ServerGroupObject instance, BaseObject oldValue, BaseObject newValue) {
        super(instance);
        oldBaseId = oldValue == null ? null : oldValue.getId();
        newBaseId = newValue == null ? null : newValue.getId();
    }

    public ServerGroupBaseChangeEventBasicImplementation(String instanceId, String oldBaseId, String newBaseId) {
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
        return EventType.SG_BASE_CHANGE;
    }
}
