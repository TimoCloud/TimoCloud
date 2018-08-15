package cloud.timo.TimoCloud.lib.json;

import com.google.gson.Gson;

import java.util.Map;

public class JsonConverter {

    private static final Gson gson = new Gson();

    public static <T> T convertMapToObject(Map map, Class<T> clazz) {
        return gson.fromJson(gson.toJsonTree(map), clazz);
    }

}
