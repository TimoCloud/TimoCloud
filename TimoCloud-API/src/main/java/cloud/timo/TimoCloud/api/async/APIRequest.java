package cloud.timo.TimoCloud.api.async;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class APIRequest {

    private APIRequestType type;
    private String id;
    private String target;
    private Map data;

    public APIRequest(APIRequestType type, String target, Map data) {
        this.type = type;
        this.target = target;
        id = generateId();

        this.data = data;
    }

    public APIRequest(APIRequestType type, String target, Object value) {
        this.type = type;
        this.target = target;
        id = generateId();

        this.data = new HashMap();
        this.data.put("value", value);
    }

    public APIRequest(APIRequestType type, Map data) {
        this(type, null, data);
    }

    public APIRequest(APIRequestType type, Object value) {
        this.type = type;
        id = generateId();
        this.data = new HashMap();
        this.data.put("value", value);
    }

    public APIRequestFuture submit() {
        TimoCloudInternalAPI.getApiRequestStorage().addRequest(this);
        TimoCloudAPI.getMessageAPI().sendMessageToCore(generatePluginMessage(data));

        APIRequestFuture future = new APIRequestFuture(this);
        return future;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    private PluginMessage generatePluginMessage(Map data) {
        return new PluginMessage("TIMOCLOUD_API_REQUEST")
                .set("type", getType().name())
                .set("id", getId())
                .set("data", data);
    }

    public APIRequestType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }

    public Map getData() {
        return data;
    }

    public static APIRequest fromMap(Map map) {
        APIRequest request = new APIRequest(
                APIRequestType.valueOf((String) map.get("type")),
                (String) map.get("target"),
                (Map) map.get("data")
        );
        request.id = (String) map.get("id");
        return request;
    }

}
