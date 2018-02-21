package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;

import java.net.InetSocketAddress;

public class CordObjectCordImplementation extends CordObjectBasicImplementation {
    public CordObjectCordImplementation() {
    }

    public CordObjectCordImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
