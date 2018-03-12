package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import cloud.timo.TimoCloud.lib.utils.InetAddressUtil;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;

public class BungeeStringHandler extends BasicStringHandler {

    @Override
    public void handleJSON(JSONObject json, String message, Channel channel) {
        if (json == null) {
            TimoCloudBungee.getInstance().severe("Error while parsing json (json is null): " + message);
            return;
        }
        String server = (String) json.get("name");
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "HANDSHAKE_SUCCESS":
                TimoCloudBungee.getInstance().onHandshakeSuccess();
                break;
            case "API_DATA":
                ((TimoCloudUniversalAPIBungeeImplementation) TimoCloudAPI.getUniversalInstance()).setData((JSONObject) data);
                break;
            case "EVENT_FIRED":
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) json.get("eventType"));
                    ((EventManager) TimoCloudAPI.getEventImplementation()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    e.printStackTrace();
                }
                break;
            case "SEND_MESSAGE_TO_SENDER": {
                TimoCloudBungee.getInstance().getTimoCloudCommand().sendMessage((String) json.get("sender"), (String) data);
            }
            case "EXECUTE_COMMAND":
                TimoCloudBungee.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloudBungee.getInstance().getProxy().getConsole(), (String) data);
                break;
            case "ADD_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().put(server, TimoCloudBungee.getInstance().getProxy().constructServerInfo(server, new InetSocketAddress((String) json.get("address"), ((Number) json.get("port")).intValue()), "", false));
                break;
            case "REMOVE_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().remove(server);
                break;
            case "SET_IP":
                try {
                    TimoCloudBungee.getInstance().getIpManager().setAddresses(
                            InetAddressUtil.getSocketAddressByName((String) json.get("CHANNEL_ADDRESS")),
                            InetAddressUtil.getSocketAddressByName((String) json.get("CLIENT_ADDRESS")));
                } catch (Exception e) {
                    TimoCloudBungee.getInstance().severe("Error while parsing IP addresses (" + json.get("CHANNEL_ADDRESS") + ", " + json.get("CLIENT_ADDRESS") + "): ");
                    e.printStackTrace();
                }
                break;
            default:
                TimoCloudBungee.getInstance().severe("Could not categorize json message: " + message);
        }
    }

}
