package cloud.timo.TimoCloud.common.gson.converter;


import cloud.timo.TimoCloud.api.async.APIRequestError;
import cloud.timo.TimoCloud.common.json.GsonFactory;
import com.google.gson.*;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * GSON serialiser/deserialiser for converting {@link APIRequestError} objects.
 */
public class APIRequestErrorConverter implements JsonSerializer<APIRequestError>, JsonDeserializer<APIRequestError> {

    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the
     * specified type. <p>
     * <p>
     * In the implementation of this call-back method, you should consider invoking
     * {@link JsonSerializationContext#serialize(Object, Type)} method to create JsonElements for any
     * non-trivial field of the {@code src} object. However, you should never invoke it on the
     * {@code src} object itself since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param src       the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @return a JsonElement corresponding to the specified object.
     */
    @SneakyThrows
    @Override
    public JsonElement serialize(APIRequestError src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("code", src.getErrorCode());
        jsonElement.addProperty("message", src.getErrorMessage());
        jsonElement.addProperty("arguments", GsonFactory.getGson().toJson(src.getArguments()));
        return jsonElement;
    }

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type. <p>
     * <p>
     * In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeOfT}
     */
    @SneakyThrows
    @Override
    public APIRequestError deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;
        Integer errorCode = jsonObject.get("code").getAsInt();
        String errorMessage = jsonObject.get("message").getAsString();
        Collection arguments = GsonFactory.getGson().fromJson(jsonObject.get("arguments").getAsString(), Collection.class);
        return new APIRequestError(errorMessage, errorCode, arguments);
    }
}

