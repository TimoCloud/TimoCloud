package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectCordImplementation extends CordObjectBasicImplementation {

    public CordObjectCordImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
