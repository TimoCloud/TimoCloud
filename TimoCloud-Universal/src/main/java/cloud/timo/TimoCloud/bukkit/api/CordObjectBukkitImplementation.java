package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;

import java.net.InetSocketAddress;

public class CordObjectBukkitImplementation extends CordObjectBasicImplementation {
    public CordObjectBukkitImplementation() {
    }

    public CordObjectBukkitImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
