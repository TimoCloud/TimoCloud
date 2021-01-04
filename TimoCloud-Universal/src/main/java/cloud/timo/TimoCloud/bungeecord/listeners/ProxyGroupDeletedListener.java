package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupDeletedEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ProxyGroupDeletedListener implements Listener {

    @EventHandler
    public void onProxyGroupDeleted(ProxyGroupDeletedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeProxyGroupName(event.getProxyGroup().getName());
    }
}
