package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.api.TimoCloudUniversalAPIVelocityImplementation;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VelocityStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudVelocity.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        String server = (String) message.get("name");
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case PROXY_HANDSHAKE_SUCCESS:
                TimoCloudVelocity.getInstance().onHandshakeSuccess();
                break;
            case API_DATA:
                ((TimoCloudUniversalAPIVelocityImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) data);
                break;
            case EVENT_FIRED:
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) message.get("eT"));
                    ((EventManager) TimoCloudAPI.getEventAPI()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    TimoCloudVelocity.getInstance().severe(e);
                }
                break;
            case CORE_SEND_MESSAGE_TO_COMMAND_SENDER: {
                TimoCloudVelocity.getInstance().getTimoCloudCommand().sendMessage((String) message.get("sender"), (String) data);
            }
            case PROXY_EXECUTE_COMMAND:
                TimoCloudVelocity.getInstance().getServer().getCommandManager().executeImmediatelyAsync(TimoCloudVelocity.getInstance().getServer().getConsoleCommandSource(), (String) data);
                break;
            case PROXY_SEND_PLAYER: {
                Map<String, Object> information = (Map<String, Object>) data;
                String playerUUID = (String) information.get("playerUUID");
                String serverObject = (String) information.get("serverObject");
                Optional<Player> player = TimoCloudVelocity.getInstance().getServer().getPlayer(UUID.fromString(playerUUID));
                if (!player.isPresent())
                    return;

                player.get().createConnectionRequest(TimoCloudVelocity.getInstance().getServer().getServer(serverObject).get()).fireAndForget();
                break;
            }
            case PROXY_ADD_SERVER:
                TimoCloudVelocity.getInstance().getServer().registerServer(new ServerInfo(server, new InetSocketAddress((String) message.get("address"), ((Number) message.get("port")).intValue())));
                break;
            case PROXY_REMOVE_SERVER:
                TimoCloudVelocity.getInstance().getServer().unregisterServer(TimoCloudVelocity.getInstance().getServer().getServer(server).get().getServerInfo());
                break;
            case CORD_SET_IP:
                try {
                    TimoCloudVelocity.getInstance().getIpManager().setAddresses(
                            InetAddressUtil.getSocketAddressByName((String) message.get("CHANNEL_ADDRESS")),
                            InetAddressUtil.getSocketAddressByName((String) message.get("CLIENT_ADDRESS")));
                } catch (Exception e) {
                    TimoCloudVelocity.getInstance().severe("Error while parsing IP addresses (" + message.get("CHANNEL_ADDRESS") + ", " + message.get("CLIENT_ADDRESS") + "): ");
                    TimoCloudVelocity.getInstance().severe(e);
                }
                break;
            case ON_PLUGIN_MESSAGE: {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                ((TimoCloudMessageAPIBasicImplementation) TimoCloudAPI.getMessageAPI()).onMessage(addressedPluginMessage);
                break;
            }
            case PROXY_STOP: {
                TimoCloudVelocity.getInstance().stop();
                break;
            }
            default:
                TimoCloudVelocity.getInstance().severe("Could not categorize json message: " + message);
        }
    }

}
