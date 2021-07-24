package cloud.timo.TimoCloud.velocity.api;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;

public class TimoCloudInternalMessageAPIVelocityImplementation implements TimoCloudInternalMessageAPI {

    @Override
    public void sendMessageToCore(String message) {
        TimoCloudVelocity.getInstance().getSocketClientHandler().sendMessage(message);
    }
}
