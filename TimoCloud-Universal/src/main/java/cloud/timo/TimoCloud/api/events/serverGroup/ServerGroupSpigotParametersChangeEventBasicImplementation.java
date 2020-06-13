package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
public class ServerGroupSpigotParametersChangeEventBasicImplementation  extends ServerGroupPropertyChangeEvent<Collection<String>> implements ServerGroupSpigotParametersChangeEvent {

    public ServerGroupSpigotParametersChangeEventBasicImplementation(ServerGroupObject instance, Collection<String> oldValue, Collection<String> newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_SPIGOT_PARAMETERS_CHANGE;
    }

}
