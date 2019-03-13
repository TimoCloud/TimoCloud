package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.events.base.*;
import cloud.timo.TimoCloud.api.events.cord.CordConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.cord.CordDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyRegisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyUnregisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxyGroup.*;
import cloud.timo.TimoCloud.api.events.server.*;
import cloud.timo.TimoCloud.api.events.serverGroup.*;

import java.util.HashMap;
import java.util.Map;

public class EventUtil {

    private static final Map<Class<? extends Event>, Class<? extends Event>> eventClassImplementations = new HashMap<>();
    private static final Map<EventType, Class<? extends Event>> eventClassesByType = new HashMap<>();

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
                PlayerServerChangeEventBasicImplementation.class,

                ProxyRegisterEventBasicImplementation.class,
                ProxyUnregisterEventBasicImplementation.class,

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
                ServerGroupDeletedEventBasicImplementation.class
        };

        for (Class<? extends Event> clazz : events) {
            for (Class interf : clazz.getInterfaces()) {
                if (! Event.class.isAssignableFrom(interf)) continue;
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

    public static Class<? extends Event> getClassByEventType(EventType eventType) {
        return eventClassesByType.get(eventType);
    }

    public static Class<? extends Event> getEventClassImplementation(Class<? extends Event> clazz) {
        return eventClassImplementations.get(clazz);
    }

    public static Map<Class<? extends Event>, Class<? extends Event>> getEventClassImplementations() {
        return eventClassImplementations;
    }
}
