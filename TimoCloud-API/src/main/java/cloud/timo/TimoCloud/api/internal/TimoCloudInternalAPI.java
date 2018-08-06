package cloud.timo.TimoCloud.api.internal;

/**
 * This is an internal API for TimoCloud - do not use it
 */
public class TimoCloudInternalAPI {

    private static TimoCloudInternalMessageAPI internalMessageAPI;

    private static APIRequestFutureStorage apiRequestStorage = new APIRequestFutureStorage();

    public static TimoCloudInternalMessageAPI getInternalMessageAPI() {
        return internalMessageAPI;
    }

    public static APIRequestFutureStorage getApiRequestStorage() {
        return apiRequestStorage;
    }
}
