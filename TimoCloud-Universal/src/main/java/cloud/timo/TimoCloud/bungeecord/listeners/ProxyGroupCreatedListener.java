package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupCreatedEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ProxyGroupCreatedListener implements Listener {

    @EventHandler
    public void onProxyGroupCreated(ProxyGroupCreatedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addProxyGroupName(event.getProxyGroup().getName());
    }
}
