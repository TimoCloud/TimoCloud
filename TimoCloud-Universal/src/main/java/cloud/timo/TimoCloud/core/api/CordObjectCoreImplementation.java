package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;

import java.net.InetSocketAddress;

public class CordObjectCoreImplementation extends CordObjectBasicImplementation {
    public CordObjectCoreImplementation() {
    }

    public CordObjectCoreImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
