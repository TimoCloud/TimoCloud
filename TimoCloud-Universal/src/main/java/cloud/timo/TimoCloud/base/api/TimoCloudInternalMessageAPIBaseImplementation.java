package cloud.timo.TimoCloud.base.api;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import cloud.timo.TimoCloud.base.TimoCloudBase;

public class TimoCloudInternalMessageAPIBaseImplementation implements TimoCloudInternalMessageAPI {
    @Override
    public void sendMessageToCore(String message) {
        TimoCloudBase.getInstance().getSocketClientHandler().sendMessage(message);
    }
}
