package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.lib.messages.Message;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import cloud.timo.TimoCloud.lib.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.lib.utils.network.InetAddressUtil;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;

public class BungeeStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBungee.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        String server = (String) message.get("name");
        String type = (String) message.get("type");
        Object data = message.get("data");
        switch (type) {
            case "HANDSHAKE_SUCCESS":
                TimoCloudBungee.getInstance().onHandshakeSuccess();
                break;
            case "API_DATA":
                ((TimoCloudUniversalAPIBungeeImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) data);
                break;
            case "EVENT_FIRED":
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) message.get("eventType"));
                    ((EventManager) TimoCloudAPI.getEventAPI()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    TimoCloudBungee.getInstance().severe(e);
                }
                break;
            case "SEND_MESSAGE_TO_SENDER": {
                TimoCloudBungee.getInstance().getTimoCloudCommand().sendMessage((String) message.get("sender"), (String) data);
            }
            case "EXECUTE_COMMAND":
                TimoCloudBungee.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloudBungee.getInstance().getProxy().getConsole(), (String) data);
                break;
            case "ADD_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().put(server, TimoCloudBungee.getInstance().getProxy().constructServerInfo(server, new InetSocketAddress((String) message.get("address"), ((Number) message.get("port")).intValue()), "", false));
                break;
            case "REMOVE_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().remove(server);
                break;
            case "SET_IP":
                try {
                    TimoCloudBungee.getInstance().getIpManager().setAddresses(
                            InetAddressUtil.getSocketAddressByName((String) message.get("CHANNEL_ADDRESS")),
                            InetAddressUtil.getSocketAddressByName((String) message.get("CLIENT_ADDRESS")));
                } catch (Exception e) {
                    TimoCloudBungee.getInstance().severe("Error while parsing IP addresses (" + message.get("CHANNEL_ADDRESS") + ", " + message.get("CLIENT_ADDRESS") + "): ");
                    TimoCloudBungee.getInstance().severe(e);
                }
                break;
            case "PLUGIN_MESSAGE": {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                ((TimoCloudMessageAPIBasicImplementation) TimoCloudAPI.getMessageAPI()).onMessage(addressedPluginMessage);
                break;
            }
            default:
                TimoCloudBungee.getInstance().severe("Could not categorize json message: " + message);
        }
    }

}
