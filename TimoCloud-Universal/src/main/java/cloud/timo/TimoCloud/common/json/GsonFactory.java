package cloud.timo.TimoCloud.common.json;

import cloud.timo.TimoCloud.common.gson.converter.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonFactory {

    private final Gson gson = new GsonBuilder();
    private static final Gson gson = Converters.registerAll(new GsonBuilder())
            .create();

    public Gson getGson() {
        return gson;
    }

    public static GsonBuilder getNewBuilder() {
        return Converters.registerAll(new GsonBuilder());
    }
}
