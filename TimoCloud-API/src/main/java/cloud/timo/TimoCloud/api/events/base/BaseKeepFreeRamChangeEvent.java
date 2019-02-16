package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public interface BaseKeepFreeRamChangeEvent extends Event {

    BaseObject getBase();

    Integer getOldValue();

    Integer getNewValue();

}
