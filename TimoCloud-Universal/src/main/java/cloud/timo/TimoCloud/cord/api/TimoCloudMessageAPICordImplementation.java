package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.TimoCloudMessageAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddressType;
import cloud.timo.TimoCloud.cord.TimoCloudCord;

public class TimoCloudMessageAPICordImplementation extends TimoCloudMessageAPIBasicImplementation implements TimoCloudMessageAPI {

    @Override
    public MessageClientAddress getOwnAddress() {
        return new MessageClientAddress(TimoCloudCord.getInstance().getName(), MessageClientAddressType.CORD);
    }
}
