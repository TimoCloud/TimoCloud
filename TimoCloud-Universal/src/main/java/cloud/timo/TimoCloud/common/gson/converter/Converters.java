package cloud.timo.TimoCloud.common.gson.converter;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * The {@code Converters} class contains static methods for registering Java converters.
 */
@SuppressWarnings({ "UnusedReturnValue", "WeakerAccess" })
public class Converters
{

    /** The specific genericized type for {@code String}. */
    public static final Type STRING_TYPE = new TypeToken<String>(){}.getType();

    /**
     * Registers all the Java converters.
     * @param builder The GSON builder to register the converters with.
     * @return A reference to {@code builder}.
     */
    public static GsonBuilder registerAll(GsonBuilder builder)
    {
        if (builder == null) { throw new NullPointerException("builder cannot be null"); }


        registerInstant(builder);

        return builder;
    }

    /**
     * Registers the {@link StringConverter} converter.
     * @param builder The GSON builder to register the converter with.
     * @return A reference to {@code builder}.
     */
    public static GsonBuilder registerInstant(GsonBuilder builder)
    {
        builder.registerTypeAdapter(STRING_TYPE, new StringConverter());

        return builder;
    }
}