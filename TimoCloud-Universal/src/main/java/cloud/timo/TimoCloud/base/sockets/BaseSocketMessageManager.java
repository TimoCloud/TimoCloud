package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import org.json.simple.JSONObject;

public class BaseSocketMessageManager {


    public void sendMessage(String type, Object data) {
        sendMessage(type, null, data);
    }

    public void sendMessage(String type, String server, Object data) {
        sendMessage(getJSON(type, server, data));
    }

    public void sendMessage(JSONObject jsonObject) {
        TimoCloudBase.getInstance().getSocketClientHandler().sendMessage(jsonObject.toString());
    }

    public JSONObject getJSON(String type, String target, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", target);
        json.put("type", type);
        json.put("data", data);
        json.put("base", TimoCloudBase.getInstance().getName());
        return json;
    }
}
