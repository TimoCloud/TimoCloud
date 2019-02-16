package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupOnlineAmountChangeEventBasicImplementation extends ServerGroupPropertyChangeEvent<Integer> implements ServerGroupOnlineAmountChangeEvent {

    public ServerGroupOnlineAmountChangeEventBasicImplementation(ServerGroupObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_ONLINE_AMOUNT_CHANGE;
    }
}
