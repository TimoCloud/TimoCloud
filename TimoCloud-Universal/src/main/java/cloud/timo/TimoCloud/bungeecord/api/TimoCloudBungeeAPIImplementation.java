package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudBungeeAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class TimoCloudBungeeAPIImplementation implements TimoCloudBungeeAPI {

    @Override
    public ProxyObject getThisProxy() {
        return TimoCloudAPI.getUniversalAPI().getProxy(TimoCloudBungee.getInstance().getProxyName());
    }
}
