package cloud.timo.TimoCloud.common.json;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonConverter {

    private final Gson GSON = GsonFactory.getGson();

    public <T> T convertMapIfNecessary(Object map, Class<T> clazz) {
        if (map == null) return null;
        if (clazz.isAssignableFrom(map.getClass())) return (T) map;
        return GSON.fromJson(GSON.toJsonTree(map), clazz);
    }

}
