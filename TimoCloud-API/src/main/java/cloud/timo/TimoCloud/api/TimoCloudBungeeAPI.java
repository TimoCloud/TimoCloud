package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ProxyObject;

/**
 * Use {@link TimoCloudAPI#getBungeeAPI()} to get an instance of this API
 */
public interface TimoCloudBungeeAPI {
    /**
     * @return The cord you are on as ProxyObject
     */
    ProxyObject getThisProxy();
}
