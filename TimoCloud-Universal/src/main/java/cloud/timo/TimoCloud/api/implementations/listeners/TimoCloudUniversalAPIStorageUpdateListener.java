package cloud.timo.TimoCloud.api.implementations.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.base.*;
import cloud.timo.TimoCloud.api.events.player.PlayerConnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEvent;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupCreatedEvent;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupDeletedEvent;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupCreatedEvent;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupDeletedEvent;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.BaseObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.ServerObjectBasicImplementation;

public class TimoCloudUniversalAPIStorageUpdateListener implements Listener {

    private final TimoCloudUniversalAPIBasicImplementation api;

    public TimoCloudUniversalAPIStorageUpdateListener(TimoCloudUniversalAPIBasicImplementation api) {
        this.api = api;
    }

    //Base Events
    @EventHandler
    public void onBaseAddressChangeEvent(BaseAddressChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setIpAddress(event.getNewValue());
    }

    @EventHandler
    public void onBaseAvaiableRamChangeEvent(BaseAvailableRamChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setAvailableRam(event.getNewValue());
    }

    @EventHandler
    public void onBaseConnectEvent(BaseConnectEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setConnected(true);
    }

    @EventHandler
    public void onBaseCpuLoadEvent(BaseCpuLoadChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setCpuLoad(event.getNewValue());
    }

    @EventHandler
    public void onBaseDisconnectEvent(BaseDisconnectEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setConnected(false);
    }

    @EventHandler
    public void onBaseKeepFreeRamChangeEvent(BaseAvailableRamChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setAvailableRam(event.getNewValue());
    }

    @EventHandler
    public void onBaseMaxCpuLoadEvent(BaseMaxCpuLoadChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setMaxCpuLoad(event.getNewValue());
    }

    @EventHandler
    public void onServerGroupCreatedEvent(ServerGroupCreatedEvent event) {
        api.getServerGroupStorage().add(event.getServerGroup());
    }

    @EventHandler
    public void onServerGroupDeletedEvent(ServerGroupDeletedEvent event) {
        api.getServerGroupStorage().remove(event.getServerGroup());
    }

    @EventHandler
    public void onProxyGroupCreatedEvent(ProxyGroupCreatedEvent event) {
        api.getProxyGroupStorage().add(event.getProxyGroup());
    }

    @EventHandler
    public void onProxyGroupDeletedEvent(ProxyGroupDeletedEvent event) {
        api.getProxyGroupStorage().remove(event.getProxyGroup());
    }

    @EventHandler
    public void onPlayerConnectEvent(PlayerConnectEvent event) {
        api.getPlayerStorage().add(event.getPlayer());
        ((ServerObjectBasicImplementation) event.getPlayer().getServer()).addPlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
        ((ProxyObjectBasicImplementation) event.getPlayer().getProxy()).addPlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        api.getPlayerStorage().remove(event.getPlayer());
        ((ServerObjectBasicImplementation) event.getPlayer().getServer()).removePlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
        ((ProxyObjectBasicImplementation) event.getPlayer().getProxy()).removePlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
    }

    @EventHandler
    public void onPlayerServerChangeEvent(PlayerServerChangeEvent event) {
        ((PlayerObjectBasicImplementation) event.getPlayer()).setServer(event.getServerTo());
        ((ServerObjectBasicImplementation) event.getServerFrom()).removePlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
        ((ServerObjectBasicImplementation) event.getServerTo()).addPlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
    }

}
