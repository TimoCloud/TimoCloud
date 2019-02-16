package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.CordObject;

public class CordObjectLink extends IdentifiableLink<CordObject> {

    public CordObjectLink(CordObject cordObject) {
        this(cordObject.getId(), cordObject.getName());
    }

    public CordObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    CordObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getCord(getId());
    }

}
