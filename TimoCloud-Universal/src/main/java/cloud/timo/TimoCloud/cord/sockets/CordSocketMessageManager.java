package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.cord.TimoCloudCord;

public class CordSocketMessageManager {

    public void sendMessage(Message message) {
        TimoCloudCord.getInstance().getSocketClientHandler().sendMessage(message.toJson());
    }

}
