package cloud.timo.TimoCloud.lib.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GenericGsonSerializer implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_PROPERTY_NAME = "class";
    private final Gson gson;

    public GenericGsonSerializer() {
        gson = new Gson();
    }

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Class actualClass;
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            String className = jsonObject.get(CLASS_PROPERTY_NAME).getAsString();
            try {
                actualClass = Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }
        }
        else {
            actualClass = typeOfT.getClass();
        }

        return gson.fromJson(json, actualClass);
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement retValue = gson.toJsonTree(src);
        if (retValue.isJsonObject()) {
            retValue.getAsJsonObject().addProperty(CLASS_PROPERTY_NAME, src.getClass().getName());
        }
        return retValue;
    }

}
