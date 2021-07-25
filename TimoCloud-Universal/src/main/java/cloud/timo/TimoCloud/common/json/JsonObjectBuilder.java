package cloud.timo.TimoCloud.common.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonObjectBuilder {

    private final JsonObject jsonObject;

    private JsonObjectBuilder() {
        this(new JsonObject());
    }

    public static JsonObjectBuilder create() {
        return new JsonObjectBuilder();
    }

    public static JsonObjectBuilder create(JsonObject jsonObject) {
        return new JsonObjectBuilder(jsonObject);
    }

    public JsonObjectBuilder set(String property, Object value) {
        if (value == null) jsonObject.add(property, JsonNull.INSTANCE);
        else if (value instanceof JsonElement) jsonObject.add(property, (JsonElement) value);
        else if (value instanceof String) jsonObject.addProperty(property, (String) value);
        else if (value instanceof Number) jsonObject.addProperty(property, (Number) value);
        else if (value instanceof Boolean) jsonObject.addProperty(property, (Boolean) value);
        else if (value instanceof Character) jsonObject.addProperty(property, (Character) value);
        else throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        return this;
    }

    public JsonObjectBuilder setIfNotNull(String property, Object value) {
        if (value == null) {
            return this;
        } else {
            return set(property, value);
        }
    }

    public JsonObjectBuilder setIfCondition(String property, Object value, boolean condition) {
        if (!condition) {
            return this;
        } else {
            return set(property, value);
        }
    }

    public JsonObjectBuilder setIfAbsent(String property, Object value) {
        if (jsonObject.has(property)) {
            return this;
        } else {
            return set(property, value);
        }
    }

    public JsonObject toJsonObject() {
        return jsonObject;
    }
}
