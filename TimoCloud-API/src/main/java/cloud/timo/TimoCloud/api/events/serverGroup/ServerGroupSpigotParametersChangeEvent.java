package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

import java.util.List;

public interface ServerGroupSpigotParametersChangeEvent  extends Event {

    ServerGroupObject getServerGroup();

    List<String> getOldValue();

    List<String> getNewValue();

}
