package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.objects.IdentifiableObject;

public interface LinkableObject<T extends IdentifiableObject> {

    IdentifiableLink<T> toLink();

}
