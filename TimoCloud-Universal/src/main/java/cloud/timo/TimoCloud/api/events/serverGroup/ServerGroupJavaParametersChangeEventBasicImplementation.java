package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ServerGroupJavaParametersChangeEventBasicImplementation  extends ServerGroupPropertyChangeEvent<List<String>> implements ServerGroupJavaParametersChangeEvent {

    public ServerGroupJavaParametersChangeEventBasicImplementation(ServerGroupObject instance, List<String> oldValue, List<String> newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_JAVA_PARAMETERS_CHANGE;
    }

}
