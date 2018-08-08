package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

public class TimoCloudInternalMessageAPIBukkitImplementation implements TimoCloudInternalMessageAPI {
    @Override
    public void sendMessageToCore(String message) {
        TimoCloudBukkit.getInstance().getSocketClientHandler().sendMessage(message);
    }
}
