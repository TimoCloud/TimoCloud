package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.CordObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectCordImplementation extends CordObjectBasicImplementation implements CordObject {

    public CordObjectCordImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
