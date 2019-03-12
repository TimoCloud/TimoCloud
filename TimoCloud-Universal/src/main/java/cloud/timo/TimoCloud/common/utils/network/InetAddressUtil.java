package cloud.timo.TimoCloud.common.utils.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InetAddressUtil {

    public static InetSocketAddress getSocketAddressByName(String address) throws Exception {
        String[] split = address.split(":");
        String hostname = Arrays.stream(Arrays.copyOfRange(split, 0, split.length - 1)).collect(Collectors.joining(":"));
        int port = Integer.parseInt(split[split.length-1]);
        return InetSocketAddress.createUnresolved(hostname, port);
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        try {
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            return InetAddress.getByName("127.0.0.1");
        }
    }

}
