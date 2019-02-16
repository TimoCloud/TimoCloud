package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public interface BaseNameChangeEvent extends Event {

    BaseObject getBase();

    String getOldValue();

    String getNewValue();

}
