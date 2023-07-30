package cloud.timo.TimoCloud.velocity.managers;

import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class IpManager {

    private Map<InetSocketAddress, InetSocketAddress> addresses;

    public IpManager() {
        addresses = new HashMap<>();
    }

    public InetSocketAddress getAddressByChannel(InetSocketAddress channel) {
        try {
            return addresses.getOrDefault(InetAddressUtil.getSocketAddressByName(channel.toString()), null);
        } catch (Exception e) {
            return null;
        }
    }

    public void setAddresses(InetSocketAddress channel, InetSocketAddress ip) {
        addresses.put(channel, ip);
    }
}
