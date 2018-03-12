package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import org.json.simple.JSONObject;

public class CordSocketMessageManager {


    public void sendMessage(String type, Object data) {
        sendMessage(type, null, data);
    }

    public void sendMessage(String type, String server, Object data) {
        sendMessage(getJSON(type, server, data));
    }

    public void sendMessage(JSONObject jsonObject) {
        TimoCloudCord.getInstance().getSocketClientHandler().sendMessage(jsonObject.toString());
    }

    public JSONObject getJSON(String type, String target, Object data) {
        return JSONBuilder.create()
                .setType(type)
                .setTarget(target)
                .setData(data)
                .set("cord", TimoCloudCord.getInstance().getName())
                .toJson();
    }
}
