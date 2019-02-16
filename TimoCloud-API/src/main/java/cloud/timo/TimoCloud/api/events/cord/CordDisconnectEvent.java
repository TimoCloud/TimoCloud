package cloud.timo.TimoCloud.api.events.cord;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.CordObject;

public interface CordDisconnectEvent extends Event {

    CordObject getCord();

}
