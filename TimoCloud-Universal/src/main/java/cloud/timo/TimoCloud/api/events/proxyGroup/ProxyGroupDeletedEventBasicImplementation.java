package cloud.timo.TimoCloud.api.events.proxyGroup;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProxyGroupDeletedEventBasicImplementation implements ProxyGroupDeletedEvent {

    private String groupId;

    public ProxyGroupDeletedEventBasicImplementation(ProxyGroup group) {
        this.groupId = group.getId();
    }

    @Override
    public ProxyGroupObject getProxyGroup() {
        return TimoCloudAPI.getUniversalAPI().getProxyGroup(groupId);
    }

    @Override
    public EventType getType() {
        return EventType.PG_DELETED;
    }
}
