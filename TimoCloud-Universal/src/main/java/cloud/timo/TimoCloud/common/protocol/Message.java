package cloud.timo.TimoCloud.common.protocol;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Message extends LinkedHashMap<String, Object> {

    private static final String TYPE_KEY = "t";
    private static final String TARGET_KEY = "@";
    private static final String DATA_KEY = "d";

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

    private Message() {
    }

    private Message(Map<String, Object> map) {
        super(map);
    }

    public static Message create() {
        return new Message();
    }

    public static Message create(Map<String, Object> map) {
        return new Message(map);
    }

    public static Message createFromJsonString(String json) {
        return new GsonBuilder()
                .create()
                .fromJson(json, Message.class);
    }

    public Message set(String key, Object value) {
        put(key, value);
        return this;
    }

    public Message setIfCondition(String key, Object value, boolean condition) {
        if (!condition) return this;

        return set(key, value);
    }

    public Message setIfNotNull(String key, Object value) {
        return setIfCondition(key, value, value != null);
    }

    public Message setIfAbsent(String key, Object value) {
        return setIfCondition(key, value, !containsKey(key));
    }

    public <T> T get(String key, Class<T> type) {
        return (T) get(key);
    }

    public MessageType getType() {
        return MessageType.fromId(((Number) get(TYPE_KEY)).intValue());
    }

    public Message setType(MessageType type) {
        set(TYPE_KEY, type.getId());
        return this;
    }

    public String getTarget() {
        return (String) get(TARGET_KEY);
    }

    public Message setTarget(String target) {
        set(TARGET_KEY, target);
        return this;
    }

    public Object getData() {
        return get(DATA_KEY);
    }

    public Message setData(Object data) {
        set(DATA_KEY, data);
        return this;
    }

    public JsonObject toJsonObject() {
        return GSON_BUILDER
                .create()
                .toJsonTree(this).getAsJsonObject();
    }

    public String toJson() {
        return GSON_BUILDER
                .create()
                .toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
