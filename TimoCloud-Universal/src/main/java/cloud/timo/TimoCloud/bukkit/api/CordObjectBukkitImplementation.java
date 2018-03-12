package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectBukkitImplementation extends CordObjectBasicImplementation {

    public CordObjectBukkitImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }

}
