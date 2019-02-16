package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public interface ServerGroupBaseChangeEvent extends Event {

    ServerGroupObject getServerGroup();

    BaseObject getOldValue();

    BaseObject getNewValue();

}
