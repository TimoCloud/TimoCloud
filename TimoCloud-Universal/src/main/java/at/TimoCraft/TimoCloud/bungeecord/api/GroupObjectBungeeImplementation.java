package at.TimoCraft.TimoCloud.bungeecord.api;

import at.TimoCraft.TimoCloud.api.implementations.GroupObjectBasicImplementation;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class GroupObjectBungeeImplementation extends GroupObjectBasicImplementation implements GroupObject {

    public GroupObjectBungeeImplementation(List<ServerObject> servers, String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        super(servers, name, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

}