package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;

import java.util.Map;

public class BukkitStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBukkit.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case SERVER_HANDSHAKE_SUCCESS:
                TimoCloudBukkit.getInstance().onHandshakeSuccess();
                break;
            case API_DATA:
                ((TimoCloudUniversalAPIBukkitImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) data);
                break;
            case EVENT_FIRED:
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) message.get("eT"));
                    ((EventManager) TimoCloudAPI.getEventAPI()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    TimoCloudBukkit.getInstance().severe(e);
                }
                break;
            case SERVER_EXECUTE_COMMAND:
                Bukkit.getScheduler().runTask(TimoCloudBukkit.getInstance(), () -> TimoCloudBukkit.getInstance().getServer().dispatchCommand(TimoCloudBukkit.getInstance().getServer().getConsoleSender(), (String) data));
                break;
            case ON_PLUGIN_MESSAGE: {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                ((TimoCloudMessageAPIBasicImplementation) TimoCloudAPI.getMessageAPI()).onMessage(addressedPluginMessage);
                break;
            }
            case SERVER_STOP: {
                TimoCloudBukkit.getInstance().stop();
                break;
            }
            default:
                TimoCloudBukkit.getInstance().severe("Error: Could not categorize json message: " + message);
        }
    }

}
