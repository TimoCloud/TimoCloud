package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public interface BaseCpuLoadChangeEvent extends Event {

    BaseObject getBase();

    Double getOldValue();

    Double getNewValue();

}
