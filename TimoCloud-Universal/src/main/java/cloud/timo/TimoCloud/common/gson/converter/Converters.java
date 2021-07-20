package cloud.timo.TimoCloud.common.gson.converter;

import cloud.timo.TimoCloud.api.async.APIRequestError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * The {@code Converters} class contains static methods for registering Java converters.
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class Converters {


    /**
     * The specific genericized type for {@code RuntimeException}.
     */
    public static final Type APIREQUESTERROR_TYPE = new TypeToken<APIRequestError>() {
    }.getType();

    /**
     * Registers all the Java converters.
     *
     * @param builder The GSON builder to register the converters with.
     * @return A reference to {@code builder}.
     */
    public static GsonBuilder registerAll(GsonBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("builder cannot be null");
        }


        registerAPIRequestError(builder);

        return builder;
    }
    /**
     * Registers the {@link APIRequestErrorConverter} converter.
     *
     * @param builder The GSON builder to register the converter with.
     * @return A reference to {@code builder}.
     */
    public static GsonBuilder registerAPIRequestError(GsonBuilder builder) {
        builder.registerTypeAdapter(APIREQUESTERROR_TYPE, new APIRequestErrorConverter());

        return builder;
    }

}