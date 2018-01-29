package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public interface TimoCloudBungeeAPI {
    /**
     * @return The proxy you are on as ProxyObject
     */
    ProxyObject getThisProxy();
}
