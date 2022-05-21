package cloud.timo.TimoCloud.common.utils.network;

import lombok.experimental.UtilityClass;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@UtilityClass
public class InetAddressUtil {

    public InetSocketAddress getSocketAddressByName(String address) {
        String[] split = address.split(":");
        String hostname = String.join(":", Arrays.copyOfRange(split, 0, split.length - 1));
        int port = Integer.parseInt(split[split.length - 1]);
        return InetSocketAddress.createUnresolved(hostname, port);
    }

    public InetAddress getLocalHost() throws UnknownHostException {
        try {
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            return InetAddress.getByName("127.0.0.1");
        }
    }

}
