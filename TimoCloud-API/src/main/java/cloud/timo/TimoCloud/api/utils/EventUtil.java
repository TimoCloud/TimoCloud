package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.events.*;

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
            default:
                return null;
        }
    }

}
