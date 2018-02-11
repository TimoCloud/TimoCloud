package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class ServerGroupObjectCoreImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectCoreImplementation(String name, List<ServerObject> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        super(name, servers, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

}