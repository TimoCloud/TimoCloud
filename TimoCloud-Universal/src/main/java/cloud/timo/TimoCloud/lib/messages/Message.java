package cloud.timo.TimoCloud.lib.messages;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class Message extends LinkedHashMap<String, Object> {

    private static final Type HASH_MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    private Message() {
    }

    private Message(Map<String, Object> map) {
        super(map);
    }

    public static Message create() {
        return new Message();
    }

    public static Message create(Map map) {
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
        return setIfCondition(key, value, ! containsKey(key));
    }

    public Message setType(String type) {
        set("type", type);
        return this;
    }

    public Message setTarget(String target) {
        set("target", target);
        return this;
    }

    public Message setData(Object data) {
        set("data", data);
        return this;
    }

    public <T> T get(String key, Class<T> type) {
        return (T) get(key);
    }

    public String getType() {
        return (String) get("type");
    }

    public String getTarget() {
        return (String) get("target");
    }

    public Object getData() {
        return get("data");
    }

    public JsonObject toJsonObject() {
        return new GsonBuilder()
                .create()
                .toJsonTree(this).getAsJsonObject();
    }

    public String toJson() {
        return new GsonBuilder()
                .create()
                .toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
