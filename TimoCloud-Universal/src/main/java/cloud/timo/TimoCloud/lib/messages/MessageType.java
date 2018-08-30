package cloud.timo.TimoCloud.lib.messages;

import java.util.HashMap;
import java.util.Map;

// NEXT FREE ID: 49

public enum MessageType {
    CORE_PARSE_COMMAND(37),
    CORE_SEND_MESSAGE_TO_COMMAND_SENDER(44),

    PROXY_HANDSHAE(32),
    PROXY_HANDSHAKE_SUCCESS(1),
    PROXY_EXECUTE_COMMAND(6),
    PROXY_STOP(22),

    PROXY_SET_PLAYER_COUNT(25),
    PROXY_TRANSFER_FINISHED(26),
    PROXY_LOG_ENTRY(27),
    PROXY_ADD_SERVER(28),
    PROXY_REMOVE_SERVER(29),

    SERVER_HANDSHAKE(33),
    SERVER_HANDSHAKE_SUCCESS(2),
    SERVER_EXECUTE_COMMAND(5),
    SERVER_SET_STATE(7),
    SERVER_SET_EXTRA(8),
    SERVER_SET_MOTD(9),
    SERVER_SET_MAP(10),
    SERVER_SET_PLAYERS(11),
    SERVER_STOP(12),

    SERVER_REGISTER(15),
    SERVER_TRANSFER_FINISHED(16),
    SERVER_LOG_ENTRY(17),

    BASE_HANDSHAKE(34),
    BASE_HANDSHAKE_SUCCESS(3),
    BASE_START_SERVER(19),
    BASE_START_PROXY(20),
    BASE_SERVER_STOPPED(21),
    BASE_PROXY_STOPPED(30),
    BASE_RESOURCES(31),
    BASE_CHECK_IF_DELETABLE(35),
    BASE_DELETE_DIRECTORY(43),
    BASE_SERVER_TEMPLATE_REQUEST(49),
    BASE_PROXY_TEMPLATE_REQUEST(50),
    BASE_SERVER_STARTED(13),
    BASE_SERVER_NOT_STARTED(14),
    BASE_PROXY_STARTED(23),
    BASE_PROXY_NOT_STARTED(24),

    CORD_HANDSHAKE(38),
    CORD_HANDSHAKE_SUCCESS(4),
    CORD_SET_IP(41),

    GET_API_DATA(36),
    API_DATA(45),
    SEND_PLUGIN_MESSAGE(39),
    ON_PLUGIN_MESSAGE(40),
    TRANSFER_TEMPLATE(42),
    TEMPLATE_TRANSFER_FINISHED(48),
    FIRE_EVENT(46),
    EVENT_FIRED(47),
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
