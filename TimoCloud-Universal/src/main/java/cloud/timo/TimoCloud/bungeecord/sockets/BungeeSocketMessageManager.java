package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import org.json.simple.JSONObject;

public class BungeeSocketMessageManager {

    public void sendMessage(String type, String target, Object data) {
        try {
            TimoCloudBungee.getInstance().getSocketClientHandler().sendMessage(getJSON(type, target, data));
        } catch (Exception e) {
            e.printStackTrace();
            TimoCloudBungee.getInstance().onSocketDisconnect();
        }
    }

    public void sendMessage(String type, Object data) {
        sendMessage(type, TimoCloudBungee.getInstance().getToken(), data);
    }

    public String getJSON(String type, String target, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", target);
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
