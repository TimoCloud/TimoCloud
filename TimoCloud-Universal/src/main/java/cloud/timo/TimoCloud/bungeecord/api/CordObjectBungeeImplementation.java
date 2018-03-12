package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.CordObjectBasicImplementation;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectBungeeImplementation extends CordObjectBasicImplementation {

    public CordObjectBungeeImplementation(String name, InetSocketAddress address, boolean connected) {
        super(name, address, connected);
    }
}
