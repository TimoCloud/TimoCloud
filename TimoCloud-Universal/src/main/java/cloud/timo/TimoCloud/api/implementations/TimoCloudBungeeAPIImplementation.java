package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudBungeeAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public class TimoCloudBungeeAPIImplementation implements TimoCloudBungeeAPI {

    private final String proxyName;

    public TimoCloudBungeeAPIImplementation(String proxyName) {
        this.proxyName = proxyName;
    }

    @Override
    public ProxyObject getThisProxy() {
        return TimoCloudAPI.getUniversalAPI().getProxy(proxyName);
    }
}
