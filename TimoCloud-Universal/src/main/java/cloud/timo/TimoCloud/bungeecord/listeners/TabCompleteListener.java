package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.base.BaseConnectEvent;
import cloud.timo.TimoCloud.api.events.base.BaseDisconnectEvent;
import cloud.timo.TimoCloud.api.events.proxy.ProxyRegisterEvent;
import cloud.timo.TimoCloud.api.events.proxy.ProxyUnregisterEvent;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupCreatedEvent;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupDeletedEvent;
import cloud.timo.TimoCloud.api.events.server.ServerRegisterEvent;
import cloud.timo.TimoCloud.api.events.server.ServerUnregisterEvent;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupCreatedEvent;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupDeletedEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class TabCompleteListener implements Listener {

    @EventHandler
    public void onBaseConnect(BaseConnectEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addBaseName(event.getBase().getName());
    }

    @EventHandler
    public void onBaseDisconnect(BaseDisconnectEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeBaseName(event.getBase().getName());
    }

    @EventHandler
    public void onProxyGroupCreated(ProxyGroupCreatedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addProxyGroupName(event.getProxyGroup().getName());
    }

    @EventHandler
    public void onProxyGroupDeleted(ProxyGroupDeletedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeProxyGroupName(event.getProxyGroup().getName());
    }

    @EventHandler
    public void onProxyRegister(ProxyRegisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addProxyName(event.getProxy().getName());
    }

    @EventHandler
    public void onProxyUnregister(ProxyUnregisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeProxyName(event.getProxy().getName());
    }

    @EventHandler
    public void onServerGroupCreated(ServerGroupCreatedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addServerGroupName(event.getServerGroup().getName());
    }

    @EventHandler
    public void onServerGroupDeleted(ServerGroupDeletedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeServerGroupName(event.getServerGroup().getName());
    }

    @EventHandler
    public void onServerRegister(ServerRegisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addServerName(event.getServer().getName());
    }

    @EventHandler
    public void onServerUnregister(ServerUnregisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeServerName(event.getServer().getName());
    }
}
