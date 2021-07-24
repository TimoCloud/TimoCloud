package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudBukkitAPI;
import cloud.timo.TimoCloud.api.TimoCloudBungeeAPI;
import cloud.timo.TimoCloud.api.TimoCloudCoreAPI;
import cloud.timo.TimoCloud.api.TimoCloudEventAPI;
import cloud.timo.TimoCloud.api.TimoCloudMessageAPI;
import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalImplementationAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class APIInstanceUtil {

    public void setUniversalInstance(TimoCloudUniversalAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudAPI.class.getDeclaredField("universalAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setBukkitInstance(TimoCloudBukkitAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudAPI.class.getDeclaredField("bukkitAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setBungeeInstance(TimoCloudBungeeAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudAPI.class.getDeclaredField("bungeeAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setCoreInstance(TimoCloudCoreAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudAPI.class.getDeclaredField("coreAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setEventInstance(TimoCloudEventAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudAPI.class.getDeclaredField("eventAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setMessageInstance(TimoCloudMessageAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudAPI.class.getDeclaredField("messageAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setInternalMessageInstance(TimoCloudInternalMessageAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudInternalAPI.class.getDeclaredField("internalMessageAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public void setInternalImplementationAPIInstance(TimoCloudInternalImplementationAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudInternalAPI.class.getDeclaredField("internalImplementationAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }
}
