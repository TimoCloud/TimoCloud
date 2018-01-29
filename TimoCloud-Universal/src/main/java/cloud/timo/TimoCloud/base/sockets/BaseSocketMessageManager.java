package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import org.json.simple.JSONObject;

public class BaseSocketMessageManager {


    public void sendMessage(String type, Object data) {
        try {
            TimoCloudBase.getInstance().getSocketClientHandler().sendMessage(getJSON(type, data));
        } catch (Exception e) {
            e.printStackTrace();
            TimoCloudBase.getInstance().onSocketDisconnect();
        }
    }

    public String getJSON(String type, Object data) {
        JSONObject json = new JSONObject();
        json.put("server", TimoCloudBase.getInstance().getName());
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
