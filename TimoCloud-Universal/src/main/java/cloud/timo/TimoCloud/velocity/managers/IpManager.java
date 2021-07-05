package cloud.timo.TimoCloud.velocity.managers;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class IpManager {

    private final Map<InetSocketAddress, InetSocketAddress> addresses;

    public IpManager() {
        addresses = new HashMap<>();
    }

    public InetSocketAddress getAddressByChannel(InetSocketAddress channel) {
        return addresses.getOrDefault(channel, null);
    }

    public void setAddresses(InetSocketAddress channel, InetSocketAddress ip) {
        addresses.put(channel, ip);
    }
}
