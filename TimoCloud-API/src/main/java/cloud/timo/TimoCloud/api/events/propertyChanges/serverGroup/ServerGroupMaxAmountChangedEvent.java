package cloud.timo.TimoCloud.api.events.propertyChanges.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public class ServerGroupMaxAmountChangedEvent extends ServerGroupPropertyChangedEvent<Integer> {

    public ServerGroupMaxAmountChangedEvent(ServerGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_MAX_ONLINE_AMOUNT_CHANGED;
    }
}
