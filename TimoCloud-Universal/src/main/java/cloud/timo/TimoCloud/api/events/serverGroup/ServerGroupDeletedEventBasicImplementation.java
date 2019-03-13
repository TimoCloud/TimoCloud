package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupDeletedEventBasicImplementation implements ServerGroupDeletedEvent {

    private String groupId;

    public ServerGroupDeletedEventBasicImplementation(ServerGroupObject group) {
        this.groupId = group.getId();
    }

    @Override
    public ServerGroupObject getServerGroup() {
        return TimoCloudAPI.getUniversalAPI().getServerGroup(groupId);
    }

    @Override
    public EventType getType() {
        return EventType.SG_DELETED;
    }
}
