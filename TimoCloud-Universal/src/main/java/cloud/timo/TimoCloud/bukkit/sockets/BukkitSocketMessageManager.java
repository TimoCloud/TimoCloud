package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.lib.messages.Message;

public class BukkitSocketMessageManager {

    public void sendMessage(Message message) {
        TimoCloudBukkit.getInstance().getSocketClientHandler().sendMessage(message.toJson());
    }
}
