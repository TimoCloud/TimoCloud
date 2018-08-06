package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.CordObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectBukkitImplementation extends CordObjectBasicImplementation implements CordObject {

    public CordObjectBukkitImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }

}
