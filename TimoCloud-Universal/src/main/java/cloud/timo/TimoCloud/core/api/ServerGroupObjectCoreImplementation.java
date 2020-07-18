package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class ServerGroupObjectCoreImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectCoreImplementation(String id, String name, Set<ServerObjectLink> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, BaseObjectLink base, int priority, Set<String> sortOutStates, List<String> javaParameters, List<String> spigotParameters, List<String> inheritedServerGroups) {
        super(id, name, servers, startupAmount, maxAmount, ram, isStatic, priority, base, sortOutStates, javaParameters, spigotParameters, inheritedServerGroups);
    }

}