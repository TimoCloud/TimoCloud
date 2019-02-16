package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseObjectLink extends IdentifiableLink<BaseObject> {

    public BaseObjectLink(BaseObject baseObject) {
        this(baseObject.getId(), baseObject.getName());
    }

    public BaseObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    BaseObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getBase(getId());
    }

}
