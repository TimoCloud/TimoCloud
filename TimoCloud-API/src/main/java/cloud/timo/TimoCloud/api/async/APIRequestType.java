package cloud.timo.TimoCloud.api.async;

import static cloud.timo.TimoCloud.api.async.APIRequestType.TargetType.*;

/**
 * Abbreviations:
 *  PG: ProxyGroup
 *  SG: ServerGroup
 *  P: Proxy
 *  S: Server
 */

public enum APIRequestType {

    G_CREATE_SERVER_GROUP               (GENERAL),
    G_CREATE_PROXY_GROUP                (GENERAL),

    PG_SET_MAX_PLAYER_COUNT             (PROXY_GROUP),
    PG_SET_MAX_PLAYER_COUNT_PER_PROXY   (PROXY_GROUP),
    PG_SET_KEEP_FREE_SLOTS              (PROXY_GROUP),
    PG_SET_MIN_AMOUNT                   (PROXY_GROUP),
    PG_SET_MAX_AMOUNT                   (PROXY_GROUP),
    PG_SET_RAM                          (PROXY_GROUP),
    PG_SET_MOTD                         (PROXY_GROUP),
    PG_SET_STATIC                       (PROXY_GROUP),
    PG_SET_PRIORITY                     (PROXY_GROUP),
    PG_SET_BASE                         (PROXY_GROUP),
    PG_SET_PROXY_CHOOSE_STRATEGY        (PROXY_GROUP),
    PG_SET_HOST_NAMES                   (PROXY_GROUP),
    PG_DELETE                           (PROXY_GROUP),

    SG_SET_MIN_AMOUNT                   (SERVER_GROUP),
    SG_SET_MAX_AMOUNT                   (SERVER_GROUP),
    SG_SET_RAM                          (SERVER_GROUP),
    SG_SET_STATIC                       (SERVER_GROUP),
    SG_SET_PRIORITY                     (SERVER_GROUP),
    SG_SET_BASE                         (SERVER_GROUP),
    SG_SET_ONLINE_AMOUNT                (SERVER_GROUP),
    SG_SET_SORT_OUT_STATES              (SERVER_GROUP),
    SG_DELETE                           (SERVER_GROUP),

    P_EXECUTE_COMMAND                   (PROXY),
    P_STOP                              (PROXY),

    S_EXECUTE_COMMAND                   (SERVER),
    S_STOP                              (SERVER),
    S_SET_STATE                         (SERVER),
    S_SET_EXTRA                         (SERVER),
    ;

    private TargetType targetType;

    APIRequestType(TargetType targetType) {
        this.targetType = targetType;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public enum TargetType {
        GENERAL,
        SERVER_GROUP,
        PROXY_GROUP,
        SERVER,
        PROXY
    }
}

