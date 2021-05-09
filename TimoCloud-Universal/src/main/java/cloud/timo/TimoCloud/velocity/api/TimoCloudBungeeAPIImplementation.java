package cloud.timo.TimoCloud.velocity.api;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudBungeeAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;

public class TimoCloudBungeeAPIImplementation implements TimoCloudBungeeAPI {

    @Override
    public ProxyObject getThisProxy() {
        return TimoCloudAPI.getUniversalAPI().getProxy(TimoCloudVelocity.getInstance().getProxyName());
    }
}
