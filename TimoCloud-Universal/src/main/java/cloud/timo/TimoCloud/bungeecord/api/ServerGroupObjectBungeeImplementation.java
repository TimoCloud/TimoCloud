package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.objects.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
public class ServerGroupObjectBungeeImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectBungeeImplementation(String id, String name, Set<ServerObject> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, BaseObject base, Set<String> sortOutStates) {
        super(id, name, servers, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }
}