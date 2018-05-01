package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudMessageAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.core.TimoCloudCore;

public class TimoCloudMessageAPICoreImplementation extends TimoCloudMessageAPIBasicImplementation implements TimoCloudMessageAPI {

    @Override
    public void sendMessage(AddressedPluginMessage message) {
        TimoCloudCore.getInstance().getPluginMessageManager().onMessage(message);
    }

    @Override
    public MessageClientAddress getOwnAddress() {
        return MessageClientAddress.CORE;
    }
}
