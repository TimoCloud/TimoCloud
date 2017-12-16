package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.json.simple.JSONObject;

public class BukkitSocketMessageManager {

    public void sendMessage(String type, String server, String data) {
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

    public String getJSON(String type, String server, String data) {
        JSONObject json = new JSONObject();
        json.put("server", server);
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
