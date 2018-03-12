package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import org.json.simple.JSONObject;

public class BukkitSocketMessageManager {

    public void sendMessage(String type, String target, Object data) {
        try {
            TimoCloudBukkit.getInstance().getSocketClientHandler().sendMessage(getJSON(type, target, data).toString());
        } catch (Exception e) {
            e.printStackTrace();
            TimoCloudBukkit.getInstance().onSocketDisconnect();
        }
    }

    public void sendMessage(String type, String data) {
        sendMessage(type, TimoCloudBukkit.getInstance().getToken(), data);
    }

    public JSONObject getJSON(String type, String target, Object data) {
        return JSONBuilder.create()
                .setType(type)
                .setTarget(target)
                .setData(data)
                .toJson();
    }
}
