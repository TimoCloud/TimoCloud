package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.CordObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.CordObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectCoreImplementation extends CordObjectBasicImplementation implements CordObject {

    public CordObjectCoreImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }

}
