package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public interface ServerMapChangeEvent extends Event {

    ServerObject getServer();

    String getOldValue();

    String getNewValue();

}
