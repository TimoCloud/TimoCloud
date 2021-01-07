package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.base.BaseDisconnectEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class BaseDisconnectListener implements Listener {

    @EventHandler
    public void onBaseDisconnect(BaseDisconnectEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeBaseName(event.getBase().getName());
    }
}
