package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ProxyGroupJavaParametersChangeEventBasicImplementation extends ProxyGroupPropertyChangeEvent<List<String>> implements ProxyGroupJavaParametersChangeEvent {

    public ProxyGroupJavaParametersChangeEventBasicImplementation(ProxyGroupObject instance, List<String> oldValue, List<String> newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.PG_JAVA_PARAMETERS_CHANGE;
    }

}
