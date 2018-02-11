package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.api.TimoCloudUniversalAPICordImplementation;
import cloud.timo.TimoCloud.sockets.BasicStringHandler;
import io.netty.channel.ChannelHandler;
import org.json.simple.JSONObject;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    @Override
    public void handleJSON(JSONObject json, String message) {
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case ("API_DATA"): {
                ((TimoCloudUniversalAPICordImplementation) TimoCloudAPI.getUniversalInstance()).setData((JSONObject) data);
                break;
            }
            default:
                TimoCloudCord.getInstance().severe("Could not categorize json message: " + message);
        }
    }
}
