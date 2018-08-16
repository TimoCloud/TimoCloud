package cloud.timo.TimoCloud.lib.json;

import com.google.gson.Gson;

public class JsonConverter {

    private static final Gson gson = new Gson();

    public static <T> T convertMapIfNecessary(Object map, Class<T> clazz) {
        if (map == null) return null;
        if (clazz.isAssignableFrom(map.getClass())) return (T) map;
        return gson.fromJson(gson.toJsonTree(map), clazz);
    }

}
