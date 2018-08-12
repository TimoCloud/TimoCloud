package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.async.APIRequestType;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class APIRequestImplementation<T> implements APIRequest<T> {
    private APIRequestType type;
    private String id;
    private String target;
    private Map data;

    public APIRequestImplementation(APIRequestType type, String target, Map data) {
        this.type = type;
        this.target = target;
        id = generateId();

        this.data = data;
    }

    public APIRequestImplementation(APIRequestType type, String target, Object value) {
        this.type = type;
        this.target = target;
        id = generateId();

        this.data = new HashMap();
        this.data.put("value", value);
    }

    public APIRequestImplementation(APIRequestType type, Map data) {
        this(type, null, data);
    }

    public APIRequestImplementation(APIRequestType type, Object value) {
        this.type = type;
        id = generateId();
        this.data = new HashMap();
        this.data.put("value", value);
    }

    @Override
    public APIRequestFuture<T> submit() {
        APIRequestFuture future = new APIRequestFutureImplementation(this);

        TimoCloudInternalAPI.getApiRequestStorage().addFuture(getId(), future);
        TimoCloudAPI.getMessageAPI().sendMessageToCore(generatePluginMessage(getData()));
        return future;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    private PluginMessage generatePluginMessage(Map data) {
        return new PluginMessage("TIMOCLOUD_API_REQUEST")
                .set("type", getType().name())
                .set("id", getId())
                .set("target", getTarget())
                .set("data", data);
    }

    @Override
    public APIRequestType getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public Map getData() {
        return data;
    }

    public static APIRequestImplementation fromMap(Map map) {
        APIRequestImplementation request = new APIRequestImplementation(
                APIRequestType.valueOf((String) map.get("type")),
                (String) map.get("target"),
                (Map) map.get("data")
        );
        request.id = (String) map.get("id");
        return request;
    }
}
