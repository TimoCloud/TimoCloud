package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.objects.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
public class ServerGroupObjectBukkitImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectBukkitImplementation(String name, Set<ServerObject> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, Set<String> sortOutStates) {
        super(name, servers, startupAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }
}
