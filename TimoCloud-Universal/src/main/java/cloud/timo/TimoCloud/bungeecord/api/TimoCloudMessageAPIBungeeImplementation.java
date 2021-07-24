package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.TimoCloudMessageAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddressType;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class TimoCloudMessageAPIBungeeImplementation extends TimoCloudMessageAPIBasicImplementation implements TimoCloudMessageAPI {

    @Override
    public MessageClientAddress getOwnAddress() {
        return new MessageClientAddress(TimoCloudBungee.getInstance().getProxyId(), MessageClientAddressType.PROXY);
    }
}
