package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;

public class VelocitySocketMessageManager {

    public void sendMessage(Message message) {
        TimoCloudVelocity.getInstance().getSocketClientHandler().sendMessage(message.toJson());
    }

}
