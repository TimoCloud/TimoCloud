package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.json.simple.JSONObject;

public class BukkitSocketMessageManager {

    public void sendMessage(String type, String server, Object data) {
        try {
            TimoCloudBukkit.getInstance().getSocketClientHandler().sendMessage(getJSON(type, server, data));
        } catch (Exception e) {
            e.printStackTrace();
            TimoCloudBukkit.getInstance().onSocketDisconnect();
        }
    }

    public void sendMessage(String type, String data) {
        sendMessage(type, TimoCloudBukkit.getInstance().getServerName(), data);
    }

    public String getJSON(String type, String server, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", server);
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
