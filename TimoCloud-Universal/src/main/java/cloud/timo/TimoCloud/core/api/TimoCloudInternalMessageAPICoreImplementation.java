package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.core.TimoCloudCore;

public class TimoCloudInternalMessageAPICoreImplementation implements TimoCloudInternalMessageAPI {
    @Override
    public void sendMessageToCore(String message) {
        TimoCloudCore.getInstance().getStringHandler().handleMessage(Message.createFromJsonString(message), message, null);
    }
}
