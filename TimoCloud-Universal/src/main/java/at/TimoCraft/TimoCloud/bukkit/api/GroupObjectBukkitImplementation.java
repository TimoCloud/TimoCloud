package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.api.implementations.GroupObjectBasicImplementation;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class GroupObjectBukkitImplementation extends GroupObjectBasicImplementation implements GroupObject {

    public GroupObjectBukkitImplementation(List<ServerObject> servers, String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        super(servers, name, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

    public GroupObjectBukkitImplementation(GroupObjectBasicImplementation groupObjectBasicImplementation) {
        this(
                groupObjectBasicImplementation.getServers(),
                groupObjectBasicImplementation.getName(),
                groupObjectBasicImplementation.getStartupAmount(),
                groupObjectBasicImplementation.getMaxAmount(),
                groupObjectBasicImplementation.getRam(),
                groupObjectBasicImplementation.isStatic(),
                groupObjectBasicImplementation.getBase(),
                groupObjectBasicImplementation.getSortOutStates());
    }

}
