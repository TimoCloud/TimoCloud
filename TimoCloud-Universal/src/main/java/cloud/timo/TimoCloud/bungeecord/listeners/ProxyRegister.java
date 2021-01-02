package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.proxy.ProxyRegisterEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ProxyRegister implements Listener {

    @EventHandler
    public void onProxyRegister(ProxyRegisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addProxyName(event.getProxy().getName());
    }
}
