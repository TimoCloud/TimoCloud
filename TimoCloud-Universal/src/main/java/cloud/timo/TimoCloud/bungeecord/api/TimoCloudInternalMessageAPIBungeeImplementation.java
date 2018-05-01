package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class TimoCloudInternalMessageAPIBungeeImplementation implements TimoCloudInternalMessageAPI {
    @Override
    public void sendMessageToCore(String message) {
        TimoCloudBungee.getInstance().getSocketClientHandler().sendMessage(message);
    }
}
