package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ProxyObject;

/**
 * Use {@link TimoCloudAPI#getProxyAPI()} to get an instance of this API
 */
public interface TimoCloudProxyAPI {
    /**
     * @return The proxy you are on as ProxyObject
     */
    ProxyObject getThisProxy();
}
