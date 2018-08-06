package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
public class ServerGroupObjectCoreImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectCoreImplementation(String name, Set<ServerObject> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, Set<String> sortOutStates) {
        super(name, servers, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

}