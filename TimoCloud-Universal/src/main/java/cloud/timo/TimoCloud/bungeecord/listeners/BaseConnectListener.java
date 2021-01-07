package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.base.BaseConnectEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class BaseConnectListener implements Listener {

    @EventHandler
    public void onBaseConnect(BaseConnectEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addBaseName(event.getBase().getName());
    }
}
