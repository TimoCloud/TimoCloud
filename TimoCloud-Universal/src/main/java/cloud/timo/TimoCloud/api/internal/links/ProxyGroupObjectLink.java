package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;

public class ProxyGroupObjectLink extends IdentifiableLink<ProxyGroupObject> {

    public ProxyGroupObjectLink(ProxyGroupObject proxyGroupObject) {
        this(proxyGroupObject.getId(), proxyGroupObject.getName());
    }

    public ProxyGroupObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    ProxyGroupObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getProxyGroup(getId());
    }

}
