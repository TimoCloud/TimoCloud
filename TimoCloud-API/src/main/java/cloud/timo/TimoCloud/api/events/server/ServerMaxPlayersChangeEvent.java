package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public interface ServerMaxPlayersChangeEvent extends Event {

    ServerObject getServer();

    Integer getOldValue();

    Integer getNewValue();

}
