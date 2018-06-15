package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.lib.messages.Message;

public class CordSocketMessageManager {

    public void sendMessage(Message message) {
        TimoCloudCord.getInstance().getSocketClientHandler().sendMessage(message.toJson());
    }

}
