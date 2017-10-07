package at.TimoCraft.TimoCloud.base.sockets;

import at.TimoCraft.TimoCloud.base.Base;
import org.json.simple.JSONObject;

public class BaseSocketMessageManager {


    public void sendMessage(String type, String data) {
        try {
            Base.getInstance().getSocketClientHandler().sendMessage(getJSON(type, data));
        } catch (Exception e) {
            e.printStackTrace();
            Base.getInstance().onSocketDisconnect();
        }
    }

    public String getJSON(String type, String data) {
        JSONObject json = new JSONObject();
        json.put("server", Base.getInstance().getName());
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }
}
