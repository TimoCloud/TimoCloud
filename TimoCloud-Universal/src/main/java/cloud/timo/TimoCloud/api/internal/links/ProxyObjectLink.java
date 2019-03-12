package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyObjectLink extends IdentifiableLink<ProxyObject> {

    public ProxyObjectLink(ProxyObject proxyObject) {
        this(proxyObject.getId(), proxyObject.getName());
    }

    public ProxyObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    ProxyObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getProxy(getId());
    }

}
