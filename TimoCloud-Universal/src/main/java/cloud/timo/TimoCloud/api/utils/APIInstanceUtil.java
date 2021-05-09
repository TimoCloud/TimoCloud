package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.api.*;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalImplementationAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalMessageAPI;

import java.lang.reflect.Field;

public class APIInstanceUtil {

    public static void setUniversalInstance(TimoCloudUniversalAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("universalAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setBukkitInstance(TimoCloudBukkitAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("bukkitAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setBungeeInstance(TimoCloudBungeeAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("bungeeAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setProxyInstance(TimoCloudProxyAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("proxyAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setCoreInstance(TimoCloudCoreAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("coreAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setEventInstance(TimoCloudEventAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("eventAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setMessageInstance(TimoCloudMessageAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudAPI.class.getDeclaredField("messageAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setInternalMessageInstance(TimoCloudInternalMessageAPI instance) throws NoSuchFieldException, IllegalAccessException{
        Field field = TimoCloudInternalAPI.class.getDeclaredField("internalMessageAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }

    public static void setInternalImplementationAPIInstance(TimoCloudInternalImplementationAPI instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = TimoCloudInternalAPI.class.getDeclaredField("internalImplementationAPI");
        field.setAccessible(true);
        field.set(null, instance);
    }
}
