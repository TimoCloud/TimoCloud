package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public interface BaseDisconnectEvent extends Event {

    BaseObject getBase();

    Boolean getOldValue();

    Boolean getNewValue();

}
