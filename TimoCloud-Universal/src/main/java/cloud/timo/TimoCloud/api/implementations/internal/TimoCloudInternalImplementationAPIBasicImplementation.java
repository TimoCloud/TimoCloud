package cloud.timo.TimoCloud.api.implementations.internal;

import cloud.timo.TimoCloud.api.implementations.objects.properties.BaseDefaultPropertiesProviderImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.properties.ProxyGroupDefaultPropertiesProviderImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.properties.ServerGroupDefaultPropertiesProviderImplementation;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalImplementationAPI;
import cloud.timo.TimoCloud.api.objects.properties.BaseProperties;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;

public class TimoCloudInternalImplementationAPIBasicImplementation implements TimoCloudInternalImplementationAPI {

    private ServerGroupProperties.ServerGroupDefaultPropertiesProvider serverGroupDefaultPropertiesProvider = new ServerGroupDefaultPropertiesProviderImplementation();
    private ProxyGroupProperties.ProxyGroupDefaultPropertiesProvider proxyGroupDefaultPropertiesProvider = new ProxyGroupDefaultPropertiesProviderImplementation();
    private BaseProperties.BaseDefaultPropertiesProvider baseDefaultPropertiesProvider = new BaseDefaultPropertiesProviderImplementation();

    @Override
    public ServerGroupProperties.ServerGroupDefaultPropertiesProvider getServerGroupDefaultPropertiesProvider() {
        return serverGroupDefaultPropertiesProvider;
    }

    @Override
    public ProxyGroupProperties.ProxyGroupDefaultPropertiesProvider getProxyGroupDefaultPropertiesProvider() {
        return proxyGroupDefaultPropertiesProvider;
    }

    @Override
    public BaseProperties.BaseDefaultPropertiesProvider getBaseDefaultPropertiesProvider() {
        return baseDefaultPropertiesProvider;
    }
}
