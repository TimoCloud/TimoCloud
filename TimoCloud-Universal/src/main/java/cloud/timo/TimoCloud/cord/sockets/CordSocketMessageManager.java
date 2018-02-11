package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.cord.TimoCloudCord;
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

    public JSONObject getJSON(String type, String server, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", server);
        json.put("type", type);
        json.put("data", data);
        json.put("cord", TimoCloudCord.getInstance().getName());
        return json;
    }
}
