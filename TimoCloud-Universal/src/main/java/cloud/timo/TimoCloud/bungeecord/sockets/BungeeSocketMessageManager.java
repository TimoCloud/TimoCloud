package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.lib.protocol.Message;

public class BungeeSocketMessageManager {

    public void sendMessage(Message message) {
        TimoCloudBungee.getInstance().getSocketClientHandler().sendMessage(message.toJson());
    }

}
