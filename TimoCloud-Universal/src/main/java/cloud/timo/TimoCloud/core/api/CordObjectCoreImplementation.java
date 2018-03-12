package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectCoreImplementation extends CordObjectBasicImplementation {

    public CordObjectCoreImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
