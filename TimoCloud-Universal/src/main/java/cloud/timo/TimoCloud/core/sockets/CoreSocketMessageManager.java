package cloud.timo.TimoCloud.core.sockets;

import org.json.simple.JSONObject;

public class CoreSocketMessageManager {

    public JSONObject getMessage(String type, String target, Object data) {
        try {
            return getJSON(type, target, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public JSONObject getMessage(String type, Object data) {
        return getMessage(type, null, data);
    }

    public JSONObject getJSON(String type, String target, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", target);
        json.put("type", type);
        json.put("data", data);
        return json;
    }
}
