package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.proxy.ProxyUnregisterEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ProxyUnregister implements Listener {

    @EventHandler
    public void onProxyUnregister(ProxyUnregisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeProxyName(event.getProxy().getName());
    }
}
