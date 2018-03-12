package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ServerGroupObjectBukkitImplementation extends ServerGroupObjectBasicImplementation implements ServerGroupObject {

    public ServerGroupObjectBukkitImplementation(String name, List<ServerObject> servers, int onlineAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        super(name, servers, onlineAmount, maxAmount, ram, isStatic, base, sortOutStates);
    }

}
