package cloud.timo.TimoCloud.lib.objects;

import org.json.simple.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONBuilder {

    private Map<String, Object> properties;

    public static JSONBuilder create() {
        return new JSONBuilder();
    }

    private JSONBuilder() {
        properties = new LinkedHashMap<>();
    }

    public boolean hasKey(String key) {
        return properties.containsKey(key);
    }

    public JSONBuilder set(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public JSONBuilder setIfNotNull(String key, Object value) {
        if (value == null) return this;
        return set(key, value);
    }

    public JSONBuilder setIfAbsent(String key, Object value) {
        if (hasKey(key)) return this;
        return set(key, value);
    }

    public JSONBuilder setIfCondition(String key, Object value, boolean condition) {
        if (! condition) return this;
        return set(key, value);
    }

    public JSONBuilder setType(String type) {
        set("type", type);
        return this;
    }

    public JSONBuilder setTarget(String target) {
        set("target", target);
        return this;
    }

    public JSONBuilder setData(Object data) {
        set("data", data);
        return this;
    }

    public JSONObject toJson() {
        return new JSONObject(properties);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
