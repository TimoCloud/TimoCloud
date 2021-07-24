package cloud.timo.TimoCloud.api.messages.objects;

import cloud.timo.TimoCloud.api.messages.exceptions.MalformedAddressException;

/**
 * A message client address is an address which identifies an application in a TimoCloud network.
 * It consists of two parts, the {@link MessageClientAddressType} and the name.
 * Examples for message client addresses would be CORE@CORE, BedWars-1@SERVER, Proxy-1@PROXY, ...
 *
 * You usually don't have to work with message client addresses, as the {@link cloud.timo.TimoCloud.api.TimoCloudMessageAPI} provides methods which create the addresses automatically.
 */
public class MessageClientAddress {

    public static final MessageClientAddress CORE = new MessageClientAddress("CORE", MessageClientAddressType.CORE);

    private final MessageClientAddressType type;
    private final String name;

    public MessageClientAddress(String name, MessageClientAddressType type) {
        this.type = type;
        this.name = name;
    }

    public MessageClientAddressType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "@" + getType().name();
    }

    public static MessageClientAddress fromString(String address) throws MalformedAddressException {
        try {
            String[] split = address.split("@");
            String name = split[0];
            String type = split[1].toUpperCase();
            return new MessageClientAddress(name, MessageClientAddressType.valueOf(type));
        } catch (Exception e) {
            throw new MalformedAddressException(address);
        }
    }

}
