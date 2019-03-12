package cloud.timo.TimoCloud.common.protocol;

import java.util.HashMap;
import java.util.Map;

// NEXT FREE ID: 49

public enum MessageType {
    CORE_PARSE_COMMAND                          (1),
    CORE_SEND_MESSAGE_TO_COMMAND_SENDER         (2),

    PROXY_HANDSHAE                              (3),
    PROXY_HANDSHAKE_SUCCESS                     (4),
    PROXY_EXECUTE_COMMAND                       (5),
    PROXY_STOP                                  (6),

    PROXY_SET_PLAYER_COUNT                      (7),
    PROXY_TRANSFER_FINISHED                     (8),
    PROXY_LOG_ENTRY                             (9),
    PROXY_ADD_SERVER                            (10),
    PROXY_REMOVE_SERVER                         (11),

    SERVER_HANDSHAKE                            (12),
    SERVER_HANDSHAKE_SUCCESS                    (13),
    SERVER_EXECUTE_COMMAND                      (14),
    SERVER_SET_STATE                            (15),
    SERVER_SET_EXTRA                            (16),
    SERVER_SET_MOTD                             (17),
    SERVER_SET_MAP                              (18),
    SERVER_SET_PLAYERS                          (19),
    SERVER_STOP                                 (20),

    SERVER_REGISTER                             (21),
    SERVER_TRANSFER_FINISHED                    (22),
    SERVER_LOG_ENTRY                            (23),

    BASE_HANDSHAKE                              (24),
    BASE_HANDSHAKE_SUCCESS                      (25),
    BASE_START_SERVER                           (26),
    BASE_START_PROXY                            (27),
    BASE_SERVER_STOPPED                         (28),
    BASE_PROXY_STOPPED                          (29),
    BASE_RESOURCES                              (30),
    BASE_CHECK_IF_DELETABLE                     (31),
    BASE_DELETE_DIRECTORY                       (32),
    BASE_SERVER_TEMPLATE_REQUEST                (33),
    BASE_PROXY_TEMPLATE_REQUEST                 (34),
    BASE_SERVER_STARTED                         (35),
    BASE_SERVER_NOT_STARTED                     (36),
    BASE_PROXY_STARTED                          (37),
    BASE_PROXY_NOT_STARTED                      (38),

    CORD_HANDSHAKE                              (39),
    CORD_HANDSHAKE_SUCCESS                      (40),
    CORD_SET_IP                                 (41),

    GET_API_DATA                                (42),
    API_DATA                                    (43),
    SEND_PLUGIN_MESSAGE                         (44),
    ON_PLUGIN_MESSAGE                           (45),
    TRANSFER_TEMPLATE                           (46),
    FIRE_EVENT                                  (47),
    EVENT_FIRED                                 (48),

    ENCRYPTION_PUBLIC_KEY                       (49), // Client sends public key to Core, Core returns ENCRYPTION_AES_KEY
    ENCRYPTION_AES_KEY                          (50)
    ;

    private static final Map<Integer, MessageType> BY_ID;

    static {
        BY_ID = new HashMap<>();
        for (MessageType messageType : values()) {
            BY_ID.put(messageType.getId(), messageType);
        }
    }

    private int id;

    MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MessageType fromId(int id) {
        return BY_ID.get(id);
    }
}
