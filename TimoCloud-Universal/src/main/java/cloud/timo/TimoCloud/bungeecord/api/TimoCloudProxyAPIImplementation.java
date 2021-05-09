package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudProxyAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;

public class TimoCloudProxyAPIImplementation implements TimoCloudProxyAPI {

    @Override
    public ProxyObject getThisProxy() {
        return TimoCloudAPI.getUniversalAPI().getProxy(TimoCloudVelocity.getInstance().getProxyName());
    }
}
