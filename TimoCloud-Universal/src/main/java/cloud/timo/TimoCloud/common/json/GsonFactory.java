package cloud.timo.TimoCloud.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonFactory {

    private final Gson gson = new GsonBuilder()
            .create();

    public Gson getGson() {
        return gson;
    }

}
