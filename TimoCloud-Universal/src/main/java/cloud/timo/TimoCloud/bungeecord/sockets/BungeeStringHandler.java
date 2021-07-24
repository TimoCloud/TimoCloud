package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import io.netty.channel.Channel;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BungeeStringHandler extends BasicStringHandler {

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBungee.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        String server = (String) message.get("name");
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case PROXY_HANDSHAKE_SUCCESS:
                TimoCloudBungee.getInstance().onHandshakeSuccess();
                break;
            case API_DATA:
                ((TimoCloudUniversalAPIBungeeImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) data);
                break;
            case EVENT_FIRED:
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) message.get("eT"));
                    ((EventManager) TimoCloudAPI.getEventAPI()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    TimoCloudBungee.getInstance().severe(e);
                }
                break;
            case CORE_SEND_MESSAGE_TO_COMMAND_SENDER: {
                TimoCloudBungee.getInstance().getTimoCloudCommand().sendMessage((String) message.get("sender"), (String) data);
            }
            case PROXY_EXECUTE_COMMAND:
                TimoCloudBungee.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloudBungee.getInstance().getProxy().getConsole(), (String) data);
                break;
            case PROXY_SEND_PLAYER: {
                Map<String, Object> information = (Map<String, Object>) data;
                String playerUUID = (String) information.get("playerUUID");
                String serverObject = (String) information.get("serverObject");
                ProxiedPlayer proxiedPlayer = TimoCloudBungee.getInstance().getProxy().getPlayer(UUID.fromString(playerUUID));
                if(Objects.isNull(proxiedPlayer))
                    return;

                proxiedPlayer.connect(TimoCloudBungee.getInstance().getProxy().getServerInfo(serverObject));
                break;
            }
            case PROXY_ADD_SERVER:
                TimoCloudBungee.getInstance().getProxy().getServers().put(server, TimoCloudBungee.getInstance().getProxy().constructServerInfo(server, new InetSocketAddress((String) message.get("address"), ((Number) message.get("port")).intValue()), "", false));
                break;
            case PROXY_REMOVE_SERVER:
                TimoCloudBungee.getInstance().getProxy().getServers().remove(server);
                break;
            case CORD_SET_IP:
                try {
                    TimoCloudBungee.getInstance().getIpManager().setAddresses(
                            InetAddressUtil.getSocketAddressByName((String) message.get("CHANNEL_ADDRESS")),
                            InetAddressUtil.getSocketAddressByName((String) message.get("CLIENT_ADDRESS")));
                } catch (Exception e) {
                    TimoCloudBungee.getInstance().severe("Error while parsing IP addresses (" + message.get("CHANNEL_ADDRESS") + ", " + message.get("CLIENT_ADDRESS") + "): ");
                    TimoCloudBungee.getInstance().severe(e);
                }
                break;
            case ON_PLUGIN_MESSAGE: {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                ((TimoCloudMessageAPIBasicImplementation) TimoCloudAPI.getMessageAPI()).onMessage(addressedPluginMessage);
                break;
            }
            case PROXY_STOP: {
                TimoCloudBungee.getInstance().stop();
                break;
            }
            default:
                TimoCloudBungee.getInstance().severe("Could not categorize json message: " + message);
        }
    }

}
