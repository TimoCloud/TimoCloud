package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ProxyObject;

public interface TimoCloudBungeeAPI {
    /**
     * @return The cord you are on as ProxyObject
     */
    ProxyObject getThisProxy();
}
