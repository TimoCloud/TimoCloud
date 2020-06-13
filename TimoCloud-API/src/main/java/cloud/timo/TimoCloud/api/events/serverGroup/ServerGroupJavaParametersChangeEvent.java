package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

import java.util.Collection;

public interface ServerGroupJavaParametersChangeEvent extends Event {

    ServerGroupObject getServerGroup();

    Collection<String> getOldValue();

    Collection<String> getNewValue();

}
