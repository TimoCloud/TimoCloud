package cloud.timo.TimoCloud.api.helpers;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.async.APIResponse;

import java.lang.reflect.Method;

public class APIRequestFutureHelper {

    public static void callRequestComplete(APIRequestFuture future, APIResponse response) {
        try {
            Method method = future.getClass().getDeclaredMethod("requestComplete", APIResponse.class);
            method.setAccessible(true);
            method.invoke(future, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
