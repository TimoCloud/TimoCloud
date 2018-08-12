package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.objects.CordObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.CordObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectBungeeImplementation extends CordObjectBasicImplementation implements CordObject {

    public CordObjectBungeeImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
