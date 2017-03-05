package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.json.simple.JSONObject;

/**
 * Created by Timo on 28.12.16.
 */
public class BukkitSocketMessageManager {


    public void sendMessage(String type, String data) {
        try {
            TimoCloudBukkit.getInstance().getSocketClientHandler().sendMessage(getJSON(type, data));
        } catch (Exception e) {
            e.printStackTrace();
            TimoCloudBukkit.getInstance().onSocketDisconnect();
        }
    }

    public String getJSON(String type, String data) {
        JSONObject json = new JSONObject();
        json.put("server", TimoCloudBukkit.getInstance().getServerName());
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
