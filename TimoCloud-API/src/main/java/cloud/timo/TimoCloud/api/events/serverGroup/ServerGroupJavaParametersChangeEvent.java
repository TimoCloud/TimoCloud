package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

import java.util.Collection;
import java.util.List;

public interface ServerGroupJavaParametersChangeEvent extends Event {

    ServerGroupObject getServerGroup();

    List<String> getOldValue();

    List<String> getNewValue();

}
