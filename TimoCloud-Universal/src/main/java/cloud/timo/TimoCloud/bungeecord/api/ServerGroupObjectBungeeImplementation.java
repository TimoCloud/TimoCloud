package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class ServerGroupObjectBungeeImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectBungeeImplementation(List<ServerObject> servers, String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        super(servers, name, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

    public ServerGroupObjectBungeeImplementation(ServerGroupObjectBasicImplementation groupObjectBasicImplementation) {
        this(
                groupObjectBasicImplementation.getServers(),
                groupObjectBasicImplementation.getName(),
                groupObjectBasicImplementation.getOnlineAmount(),
                groupObjectBasicImplementation.getMaxAmount(),
                groupObjectBasicImplementation.getRam(),
                groupObjectBasicImplementation.isStatic(),
                groupObjectBasicImplementation.getBase(),
                groupObjectBasicImplementation.getSortOutStates());
    }

}