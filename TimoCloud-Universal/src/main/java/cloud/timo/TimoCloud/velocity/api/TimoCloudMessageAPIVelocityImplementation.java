package cloud.timo.TimoCloud.velocity.api;

import cloud.timo.TimoCloud.api.TimoCloudMessageAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddressType;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;

public class TimoCloudMessageAPIVelocityImplementation extends TimoCloudMessageAPIBasicImplementation implements TimoCloudMessageAPI {
    @Override
    public MessageClientAddress getOwnAddress() {
        return new MessageClientAddress(TimoCloudVelocity.getInstance().getProxyId(), MessageClientAddressType.PROXY);
    }
}
