package cloud.timo.TimoCloud.api.internal;

import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;

/**
 * Provides implementations for interfaces in the API
 */
public interface TimoCloudInternalImplementationAPI {

    ServerGroupProperties.ServerGroupDefaultPropertiesProvider getServerGroupDefaultPropertiesProvider();

    ProxyGroupProperties.ProxyGroupDefaultPropertiesProvider getProxyGroupDefaultPropertiesProvider();

}
