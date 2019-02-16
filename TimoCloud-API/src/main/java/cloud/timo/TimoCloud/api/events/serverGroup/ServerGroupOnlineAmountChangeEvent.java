package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public interface ServerGroupOnlineAmountChangeEvent extends Event {

    ServerGroupObject getServerGroup();

    Integer getOldValue();

    Integer getNewValue();
}
