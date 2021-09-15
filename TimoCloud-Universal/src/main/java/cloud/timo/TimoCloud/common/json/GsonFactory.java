package cloud.timo.TimoCloud.common.json;

import cloud.timo.TimoCloud.common.gson.converter.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {

    private static final Gson gson = Converters.registerAll(new GsonBuilder())
            .create();

    public static Gson getGson() {
        return gson;
    }

    public static GsonBuilder getNewBuilder() {
        return Converters.registerAll(new GsonBuilder());
    }

}
