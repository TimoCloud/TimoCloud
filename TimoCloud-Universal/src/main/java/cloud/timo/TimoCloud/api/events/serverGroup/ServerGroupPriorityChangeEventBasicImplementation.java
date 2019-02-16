package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupPriorityChangeEventBasicImplementation extends ServerGroupPropertyChangeEvent<Integer> implements ServerGroupPriorityChangeEvent {

    public ServerGroupPriorityChangeEventBasicImplementation(ServerGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_PRIORITY_CHANGE;
    }
}
