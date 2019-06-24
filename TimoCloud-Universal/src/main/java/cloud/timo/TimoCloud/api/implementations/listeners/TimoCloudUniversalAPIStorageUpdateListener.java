package cloud.timo.TimoCloud.api.implementations.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.base.*;
import cloud.timo.TimoCloud.api.events.cord.CordConnectEvent;
import cloud.timo.TimoCloud.api.events.cord.CordDisconnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerConnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEvent;
import cloud.timo.TimoCloud.api.events.proxy.ProxyRegisterEvent;
import cloud.timo.TimoCloud.api.events.proxy.ProxyUnregisterEvent;
import cloud.timo.TimoCloud.api.events.proxyGroup.*;
import cloud.timo.TimoCloud.api.events.server.*;
import cloud.timo.TimoCloud.api.events.serverGroup.*;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.*;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

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
    public void onBaseReadyEvent(BaseReadyEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setReady(true);
    }

    @EventHandler
    public void onBasePublicAddressChangeEvent(BasePublicAddressChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setIpAddress(event.getNewValue());
    }

    @EventHandler
    public void onBaseNotReadyEvent(BaseNotReadyEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setReady(false);
    }

    @EventHandler
    public void onBaseNameChangeEvent(BaseNameChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setName(event.getNewValue());
    }

    @EventHandler
    public void onBaseMaxRamChangeEvent(BaseMaxRamChangeEvent event) {
        ((BaseObjectBasicImplementation) event.getBase()).setMaxRam(event.getNewValue());
    }

    //Cord Events
    @EventHandler
    public void onCordDisconnectEvent(CordDisconnectEvent event) {
        api.getCordStorage().remove(event.getCord());
    }

    @EventHandler
    public void onCordConnectEvent(CordConnectEvent event) {
        api.getCordStorage().add(event.getCord());
    }

    //Player Events
    @EventHandler
    public void onPlayerConnectEvent(PlayerConnectEvent event) {
        api.getPlayerStorage().add(event.getPlayer());
        ((ServerObjectBasicImplementation) event.getPlayer().getServer()).addPlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
        ((ProxyObjectBasicImplementation) event.getPlayer().getProxy()).addPlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        api.getPlayerStorage().remove(event.getPlayer());
        ServerObjectBasicImplementation server = ((ServerObjectBasicImplementation) event.getPlayer().getServer());
        if (server != null) server.removePlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
        ((ProxyObjectBasicImplementation) event.getPlayer().getProxy()).removePlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
    }

    @EventHandler
    public void onPlayerServerChangeEvent(PlayerServerChangeEvent event) {
        api.getPlayerStorage().update(event.getPlayer());
        ((PlayerObjectBasicImplementation) event.getPlayer()).setServer(event.getServerTo());
        ServerObjectBasicImplementation serverFrom = (ServerObjectBasicImplementation) event.getServerFrom();
        if (serverFrom != null) serverFrom.removePlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());
        ((ServerObjectBasicImplementation) event.getServerTo()).addPlayer(((PlayerObjectBasicImplementation) event.getPlayer()).toLink());

    }

    //ProxyGroup Events
    @EventHandler
    public void onProxyRegisterEvent(ProxyRegisterEvent event) {
        ProxyObject proxy = event.getProxy();
        api.getProxyStorage().add(proxy);
        ((ProxyGroupObjectBasicImplementation) proxy.getGroup()).addProxyInternally(((ProxyObjectBasicImplementation) proxy).toLink());
    }

    @EventHandler
    public void onProxyUnregisterEvent(ProxyUnregisterEvent event) {
        ProxyObject proxy = event.getProxy();
        api.getProxyStorage().remove(proxy);
        ((ProxyGroupObjectBasicImplementation) proxy.getGroup()).removeProxyInternally(((ProxyObjectBasicImplementation) proxy).toLink());
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
    public void onProxyGroupBaseChangeEvent(ProxyGroupBaseChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setBaseInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupKeepFreeSlotsChangeEvent(ProxyGroupKeepFreeSlotsChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setKeepFreeSlotsInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupMaxAmountChangeEvent(ProxyGroupMaxAmountChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setMaxAmoutInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupMaxPlayerCountChangeEvent(ProxyGroupMaxPlayerCountChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setMaxPlayerCountInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupMaxPlayerCountPerProxyChangeEvent(ProxyGroupMaxPlayerCountPerProxyChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setMaxPlayerCountPerProxyInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupMinAmountChangeEvent(ProxyGroupMinAmountChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setMinAmountInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupMotdChangeEvent(ProxyGroupMotdChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setMotdInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupPriorityChangeEvent(ProxyGroupPriorityChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setPriorityInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupProxyChooseStrategyChangeEvent(ProxyGroupProxyChooseStrategyChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setProxyChooseStrategyInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupRamChangeEvent(ProxyGroupRamChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setRamInternally(event.getNewValue());
    }

    @EventHandler
    public void onProxyGroupStaticChangeEvent(ProxyGroupStaticChangeEvent event) {
        ((ProxyGroupObjectBasicImplementation) event.getProxyGroup()).setStaticInternally(event.getNewValue());
    }

    //Server Events
    @EventHandler
    public void onServerRegisterEvent(ServerRegisterEvent event) {
        ServerObject server = event.getServer();
        api.getServerStorage().add(server);
        ((ServerGroupObjectBasicImplementation) server.getGroup()).addServerInternally(((ServerObjectBasicImplementation) server).toLink());
    }

    @EventHandler
    public void onServerUnregisterEvent(ServerUnregisterEvent event) {
        ServerObject server = event.getServer();
        api.getServerStorage().remove(server);
        ((ServerGroupObjectBasicImplementation) server.getGroup()).removeServerInternally(((ServerObjectBasicImplementation) server).toLink());
    }

    @EventHandler
    public void onServerExtraChangeEvent(ServerExtraChangeEvent event) {
        ((ServerObjectBasicImplementation) event.getServer()).setExtraInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerMapChangeEvent(ServerMapChangeEvent event) {
        ((ServerObjectBasicImplementation) event.getServer()).setMapInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerMaxPlayersChangeEvent(ServerMaxPlayersChangeEvent event) {
        ((ServerObjectBasicImplementation) event.getServer()).setMaxPlayerCountInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerMotdChangeEvent(ServerMotdChangeEvent event) {
        ((ServerObjectBasicImplementation) event.getServer()).setMotdInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerOnlinePlayerCountChangeEvent(ServerOnlinePlayerCountChangeEvent event) {
        ((ServerObjectBasicImplementation) event.getServer()).setOnlinePlayerCountInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerStateChangeEvent(ServerStateChangeEvent event) {
        ((ServerObjectBasicImplementation) event.getServer()).setStateInternally(event.getNewValue());
    }

    //ServerGroup Events
    @EventHandler
    public void onServerGroupCreatedEvent(ServerGroupCreatedEvent event) {
        api.getServerGroupStorage().add(event.getServerGroup());
    }

    @EventHandler
    public void onServerGroupDeletedEvent(ServerGroupDeletedEvent event) {
        api.getServerGroupStorage().remove(event.getServerGroup());
    }

    @EventHandler
    public void onServerGroupBaseChangeEvent(ServerGroupBaseChangeEvent event) {
        ((ServerGroupObjectBasicImplementation) event.getServerGroup()).setBaseInternally(new BaseObjectLink(event.getNewValue()));
    }

    @EventHandler
    public void onServerGroupMaxAmountChangeEvent(ServerGroupMaxAmountChangeEvent event) {
        ((ServerGroupObjectBasicImplementation) event.getServerGroup()).setMaxAmoutInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerGroupOnlineAmountChangeEvent(ServerGroupOnlineAmountChangeEvent event) {
        ((ServerGroupObjectBasicImplementation) event.getServerGroup()).setOnlineAmountInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerGroupPriorityChangeEvent(ServerGroupPriorityChangeEvent event) {
        ((ServerGroupObjectBasicImplementation) event.getServerGroup()).setPriorityInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerGroupRamChangeEvent(ServerGroupRamChangeEvent event) {
        ((ServerGroupObjectBasicImplementation) event.getServerGroup()).setRamInternally(event.getNewValue());
    }

    @EventHandler
    public void onServerGroupStaticChangeEvent(ServerGroupStaticChangeEvent event) {
        ((ServerGroupObjectBasicImplementation) event.getServerGroup()).setStaticInternally(event.getNewValue());
    }

}
