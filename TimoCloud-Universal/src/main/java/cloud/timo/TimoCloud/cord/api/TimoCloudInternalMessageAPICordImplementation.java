package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import cloud.timo.TimoCloud.cord.TimoCloudCord;

public class TimoCloudInternalMessageAPICordImplementation implements TimoCloudInternalMessageAPI {

    @Override
    public void sendMessageToCore(String message) {
        TimoCloudCord.getInstance().getSocketClientHandler().sendMessage(message);
    }

}
