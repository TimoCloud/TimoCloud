package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.sockets.BasicStringHandler;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;

public class BungeeStringHandler extends BasicStringHandler {


    public void handleJSON(JSONObject json, String message) {
        if (json == null) {
            TimoCloudBungee.severe("Error while parsing json (json is null): " + message);
            return;
        }
        String server = (String) json.get("name");
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "API_DATA":
                ((TimoCloudUniversalAPIBungeeImplementation) TimoCloudAPI.getUniversalInstance()).setData((JSONObject) data);
                break;
            case "EXECUTE_COMMAND":
                TimoCloudBungee.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloudBungee.getInstance().getProxy().getConsole(), (String) data);
                break;
            case "ADD_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().put(server, TimoCloudBungee.getInstance().getProxy().constructServerInfo(server, new InetSocketAddress((String) json.get("address"), ((Long) json.get("port")).intValue()), "", false));
                break;
            case "REMOVE_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().remove(server);
                break;
            default:
                TimoCloudBungee.severe("Could not categorize json message: " + message);
        }
    }

}
