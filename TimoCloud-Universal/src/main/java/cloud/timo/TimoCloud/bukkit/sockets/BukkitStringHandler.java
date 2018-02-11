package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.sockets.BasicStringHandler;
import org.json.simple.JSONObject;

public class BukkitStringHandler extends BasicStringHandler {

    public void handleJSON(JSONObject json, String message) {
        if (json == null) {
            TimoCloudBukkit.log("Error while parsing json (json is null): " + message);
            return;
        }
        String server = (String) json.get("target");
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "API_DATA":
                ((TimoCloudUniversalAPIBukkitImplementation) TimoCloudAPI.getUniversalInstance()).setData((JSONObject) data);
                break;
            case "EXECUTE_COMMAND":
                TimoCloudBukkit.getInstance().getServer().dispatchCommand(TimoCloudBukkit.getInstance().getServer().getConsoleSender(), (String) data);
                break;
            default:
                TimoCloudBukkit.log("Error: Could not categorize json message: " + message);
        }
    }

}
