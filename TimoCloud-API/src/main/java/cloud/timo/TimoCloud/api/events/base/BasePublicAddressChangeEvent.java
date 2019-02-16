package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.BaseObject;

import java.net.InetAddress;

public interface BasePublicAddressChangeEvent extends Event {

    BaseObject getBase();

    InetAddress getOldValue();

    InetAddress getNewValue();

}
