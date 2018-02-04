package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class ServerGroupObjectBukkitImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectBukkitImplementation(List<ServerObject> servers, String name, int onlineAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        super(servers, name, onlineAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

    public ServerGroupObjectBukkitImplementation(ServerGroupObjectBasicImplementation groupObjectBasicImplementation) {
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
