package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.events.*;
import cloud.timo.TimoCloud.api.events.propertyChanges.base.*;
import cloud.timo.TimoCloud.api.events.propertyChanges.proxyGroup.*;
import cloud.timo.TimoCloud.api.events.propertyChanges.server.*;
import cloud.timo.TimoCloud.api.events.propertyChanges.serverGroup.*;

public class EventUtil {

    public static Class<? extends Event> getClassByEventType(EventType eventType) {
        switch (eventType) {
            case PLAYER_CONNECT:
                return PlayerConnectEvent.class;
            case PLAYER_DISCONNECT:
                return PlayerDisconnectEvent.class;
            case PLAYER_SERVER_CHANGE:
                return PlayerServerChangeEvent.class;
            case SERVER_REGISTER:
                return ServerRegisterEvent.class;
            case SERVER_UNREGISTER:
                return ServerUnregisterEvent.class;
            case PROXY_REGISTER:
                return ProxyRegisterEvent.class;
            case PROXY_UNREGISTER:
                return ProxyUnregisterEvent.class;
            case CORD_CONNECT:
                return CordConnectEvent.class;
            case CORD_DISCONNECT:
                return CordDisconnectEvent.class;
            case PG_MAX_AMOUNT_CHANGED:
                return ProxyGroupMaxAmountChangedEvent.class;
            case PG_MIN_AMOUNT_CHANGED:
                return ProxyGroupMinAmountChangedEvent.class;
            case PG_KEEP_FREE_SLOTS_CHANGED:
                return ProxyGroupKeepFreeSlotsChangedEvent.class;
            case PG_MAX_PLAYER_COUNT_CHANGED:
                return ProxyGroupMaxPlayerCountChangedEvent.class;
            case PG_MAX_PLAYER_COUNT_PER_PROXY_CHANGED:
                return ProxyGroupMaxPlayerCountPerProxyChangedEvent.class;
            case PG_BASE_CHANGED:
                return ProxyGroupBaseChangedEvent.class;
            case PG_MOTD_CHANGED:
                return ProxyGroupMotdChangedEvent.class;
            case PG_PRIORITY_CHANGED:
                return ProxyGroupPriorityChangedEvent.class;
            case PG_PROXY_CHOOSE_STRATEGY_CHANGED:
                return ProxyGroupProxyChooseStrategyChangedEvent.class;
            case PG_RAM_CHANGED:
                return ProxyGroupRamChangedEvent.class;
            case PG_STATIC_CHANGED:
                return ProxyGroupStaticChangedEvent.class;
            case B_NAME_CHANGED:
                return BaseNameChangedEvent.class;
            case B_ADDRESS_CHANGED:
                return BaseAddressChangedEvent.class;
            case B_PUBLIC_ADDRESS_CHANGED:
                return BasePublicAddressChangedEvent.class;
            case B_AVAILABLE_RAM_CHANGED:
                return BaseAvailableRamChangedEvent.class;
            case B_MAX_RAM_CHANGED:
                return BaseMaxRamChangedEvent.class;
            case B_KEEP_FREE_RAM_CHANGED:
                return BaseKeepFreeRamChangedEvent.class;
            case B_CPU_LOAD_CHANGED:
                return BaseCpuLoadChangedEvent.class;
            case B_MAX_CPU_LOAD_CHANGED:
                return BaseMaxCpuLoadChangedEvent.class;
            case B_CONNECTED_CHANGED:
                return BaseConnectedChangedEvent.class;
            case B_READY_CHANGED:
                return BaseReadyChangedEvent.class;
            case S_STATE_CHANGED:
                return ServerStateChangedEvent.class;
            case S_EXTRA_CHANGED:
                return ServerExtraChangedEvent.class;
            case S_MOTD_CHANGED:
                return ServerMotdChangedEvent.class;
            case S_ONLINE_PLAYER_COUNT_CHANGED:
                return ServerOnlinePlayerCountChangedEvent.class;
            case S_MAX_PLAYERS_CHANGED:
                return ServerMaxPlayersChangedEvent.class;
            case S_MAP_CHANGED:
                return ServerMapChangedEvent.class;
            case SG_ONLINE_AMOUNT_CHANGED:
                return ServerGroupOnlineAmountChangedEvent.class;
            case SG_MAX_ONLINE_AMOUNT_CHANGED:
                return ServerGroupMaxAmountChangedEvent.class;
            case SG_RAM_CHANGED:
                return ServerGroupRamChangedEvent.class;
            case SG_STATIC_CHANGED:
                return ServerGroupStaticChangedEvent.class;
            case SG_PRIORITY_CHANGED:
                return ServerGroupPriorityChangedEvent.class;
            case SG_BASE_CHANGED:
                return ServerGroupBaseChangedEvent.class;
            default:
                return null;
        }
    }

}
