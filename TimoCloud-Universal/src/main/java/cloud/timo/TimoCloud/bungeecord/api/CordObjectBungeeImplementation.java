package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;

import java.net.InetSocketAddress;

public class CordObjectBungeeImplementation extends CordObjectBasicImplementation {
    public CordObjectBungeeImplementation() {
    }

    public CordObjectBungeeImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
