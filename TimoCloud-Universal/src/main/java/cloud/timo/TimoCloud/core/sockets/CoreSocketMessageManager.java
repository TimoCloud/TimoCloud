package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import org.json.simple.JSONObject;

public class CoreSocketMessageManager {

    public JSONObject getMessage(String type, String server, Object data) {
        try {
            return getJSON(type, server, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public JSONObject getMessage(String type, Object data) {
        return getMessage(type, null, data);
    }

    public JSONObject getJSON(String type, String server, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", server);
        json.put("type", type);
        json.put("data", data);
        return json;
    }
}
