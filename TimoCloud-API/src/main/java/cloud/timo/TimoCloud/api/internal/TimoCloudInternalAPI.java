package cloud.timo.TimoCloud.api.internal;

/**
 * This is an internal API for TimoCloud - do not use it
 */
public class TimoCloudInternalAPI {

    private static TimoCloudInternalMessageAPI internalMessageAPI;
    private static final APIRequestFutureStorage apiRequestStorage = new APIRequestFutureStorage();
    private static TimoCloudInternalImplementationAPI internalImplementationAPI;

    public static TimoCloudInternalMessageAPI getInternalMessageAPI() {
        return internalMessageAPI;
    }

    public static APIRequestFutureStorage getApiRequestStorage() {
        return apiRequestStorage;
    }

    public static TimoCloudInternalImplementationAPI getImplementationAPI() {
        return internalImplementationAPI;
    }
}
