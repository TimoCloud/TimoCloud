package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.Main;
import org.json.simple.JSONObject;

/**
 * Created by Timo on 28.12.16.
 */
public class SocketMessageManager {


    public void sendMessage(String type, String data) {
        try {
            Main.getInstance().getSocketClientHandler().sendMessage(getJSON(type, data));
        } catch (Exception e) {
            e.printStackTrace();
            Main.getInstance().onSocketDisconnect();
        }
    }

    public String getJSON(String type, String data) {
        JSONObject json = new JSONObject();
        json.put("server", Main.getInstance().getServerName());
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
