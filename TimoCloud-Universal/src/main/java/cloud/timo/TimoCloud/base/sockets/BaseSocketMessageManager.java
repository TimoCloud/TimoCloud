package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.lib.protocol.Message;

public class BaseSocketMessageManager {

    public void sendMessage(Message message) {
        TimoCloudBase.getInstance().getSocketClientHandler().sendMessage(message.toJson());
    }

}
