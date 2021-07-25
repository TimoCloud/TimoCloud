package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.events.base.BaseAddressChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseAvailableRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseCpuLoadChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseKeepFreeRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseMaxCpuLoadChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseMaxRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseNameChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseNotReadyEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BasePublicAddressChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseReadyEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.cord.CordConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.cord.CordDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyOnlinePlayerCountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyRegisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyUnregisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupBaseChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupCreatedEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupDeletedEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupJavaParametersChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupKeepFreeSlotsChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupMaxAmountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupMaxPlayerCountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupMaxPlayerCountPerProxyChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupMinAmountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupMotdChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupPriorityChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupProxyChooseStrategyChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupStaticChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerExtraChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerMapChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerMaxPlayersChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerMotdChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerOnlinePlayerCountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerRegisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerStateChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.server.ServerUnregisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupBaseChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupCreatedEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupDeletedEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupJavaParametersChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupMaxAmountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupOnlineAmountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupPriorityChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupSpigotParametersChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupStaticChangeEventBasicImplementation;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EventUtil {

    private final Map<Class<? extends Event>, Class<? extends Event>> eventClassImplementations = new HashMap<>();
    private final Map<EventType, Class<? extends Event>> eventClassesByType = new HashMap<>();

    static {
        final Class[] events = {
                BaseAddressChangeEventBasicImplementation.class,
                BaseAvailableRamChangeEventBasicImplementation.class,
                BaseConnectEventBasicImplementation.class,
                BaseCpuLoadChangeEventBasicImplementation.class,
                BaseDisconnectEventBasicImplementation.class,
                BaseKeepFreeRamChangeEventBasicImplementation.class,
                BaseMaxCpuLoadChangeEventBasicImplementation.class,
                BaseMaxRamChangeEventBasicImplementation.class,
                BaseNameChangeEventBasicImplementation.class,
                BaseNotReadyEventBasicImplementation.class,
                BasePublicAddressChangeEventBasicImplementation.class,
                BaseReadyEventBasicImplementation.class,

                CordConnectEventBasicImplementation.class,
                CordDisconnectEventBasicImplementation.class,
                PlayerConnectEventBasicImplementation.class,
                PlayerDisconnectEventBasicImplementation.class,
                PlayerServerChangeEventBasicImplementation.class,

                ProxyRegisterEventBasicImplementation.class,
                ProxyUnregisterEventBasicImplementation.class,
                ProxyOnlinePlayerCountChangeEventBasicImplementation.class,

                ProxyGroupCreatedEventBasicImplementation.class,
                ProxyGroupBaseChangeEventBasicImplementation.class,
                ProxyGroupKeepFreeSlotsChangeEventBasicImplementation.class,
                ProxyGroupMaxAmountChangeEventBasicImplementation.class,
                ProxyGroupMaxPlayerCountChangeEventBasicImplementation.class,
                ProxyGroupMaxPlayerCountPerProxyChangeEventBasicImplementation.class,
                ProxyGroupMinAmountChangeEventBasicImplementation.class,
                ProxyGroupMotdChangeEventBasicImplementation.class,
                ProxyGroupPriorityChangeEventBasicImplementation.class,
                ProxyGroupProxyChooseStrategyChangeEventBasicImplementation.class,
                ProxyGroupRamChangeEventBasicImplementation.class,
                ProxyGroupStaticChangeEventBasicImplementation.class,
                ProxyGroupDeletedEventBasicImplementation.class,
                ProxyGroupJavaParametersChangeEventBasicImplementation.class,

                ServerExtraChangeEventBasicImplementation.class,
                ServerMapChangeEventBasicImplementation.class,
                ServerMaxPlayersChangeEventBasicImplementation.class,
                ServerMotdChangeEventBasicImplementation.class,
                ServerOnlinePlayerCountChangeEventBasicImplementation.class,
                ServerRegisterEventBasicImplementation.class,
                ServerStateChangeEventBasicImplementation.class,
                ServerUnregisterEventBasicImplementation.class,

                ServerGroupCreatedEventBasicImplementation.class,
                ServerGroupBaseChangeEventBasicImplementation.class,
                ServerGroupMaxAmountChangeEventBasicImplementation.class,
                ServerGroupOnlineAmountChangeEventBasicImplementation.class,
                ServerGroupPriorityChangeEventBasicImplementation.class,
                ServerGroupRamChangeEventBasicImplementation.class,
                ServerGroupStaticChangeEventBasicImplementation.class,
                ServerGroupDeletedEventBasicImplementation.class,
                ServerGroupJavaParametersChangeEventBasicImplementation.class,
                ServerGroupSpigotParametersChangeEventBasicImplementation.class
        };

        for (Class<? extends Event> clazz : events) {
            for (Class interf : clazz.getInterfaces()) {
                if (!Event.class.isAssignableFrom(interf)) continue;
                eventClassImplementations.put(interf, clazz);
                break;
            }
            try {
                Event event = clazz.newInstance();
                eventClassesByType.put(event.getType(), clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Class<? extends Event> getClassByEventType(EventType eventType) {
        return eventClassesByType.get(eventType);
    }

    public Class<? extends Event> getEventClassImplementation(Class<? extends Event> clazz) {
        return eventClassImplementations.get(clazz);
    }

    public Map<Class<? extends Event>, Class<? extends Event>> getEventClassImplementations() {
        return eventClassImplementations;
    }
}
