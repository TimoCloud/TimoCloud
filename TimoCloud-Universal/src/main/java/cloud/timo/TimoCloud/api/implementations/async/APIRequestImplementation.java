package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.async.APIRequestType;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.common.datatypes.TypeMap;
import cloud.timo.TimoCloud.common.utils.RandomIdGenerator;

import java.util.HashMap;
import java.util.Map;

public class APIRequestImplementation<T> implements APIRequest<T> {

    private final APIRequestType type;
    private String id;
    private String target;
    private Map<Object, Object> data;

    public APIRequestImplementation(APIRequestType type) {
        this.type = type;
        id = generateId();
    }

    public APIRequestImplementation(APIRequestType type, String target, Map<Object, Object> data) {
        this(type);
        this.target = target;

        this.data = data;
    }

    public APIRequestImplementation(APIRequestType type, String target, Object value) {
        this(type);
        this.target = target;

        this.data = new HashMap<>();
        this.data.put("value", value);
    }

    public APIRequestImplementation(APIRequestType type, String target) {
        this(type, target, new HashMap<>());
    }

    public APIRequestImplementation(APIRequestType type, Map<?, ?> data) {
        this(type, null, data);
    }

    public APIRequestImplementation(APIRequestType type, Object value) {
        this(type, new TypeMap().put("value", value));
    }

    @Override
    public APIRequestFuture<T> submit() {
        APIRequestFuture<T> future = new APIRequestFutureImplementation<>(this);

        TimoCloudInternalAPI.getApiRequestStorage().addFuture(getId(), future);
        TimoCloudAPI.getMessageAPI().sendMessageToCore(generatePluginMessage(getData()));
        return future;
    }

    private static String generateId() {
        return RandomIdGenerator.generateId();
    }

    private PluginMessage generatePluginMessage(Map<?, ?> data) {
        return new PluginMessage("TIMOCLOUD_API_REQUEST")
                .set("rtype", getType().name())
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
    public Map<?, ?> getData() {
        return data;
    }

    public static APIRequestImplementation<?> fromMap(Map<?, ?> map) {
        APIRequestImplementation<?> request = new APIRequestImplementation<>(
                APIRequestType.valueOf((String) map.get("rtype")),
                (String) map.get("target"),
                map.get("data")
        );
        request.id = (String) map.get("id");
        return request;
    }
}
