package cloud.timo.TimoCloud.velocity.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;

public class TimoCloudUniversalAPIVelocityImplementation extends TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    public TimoCloudUniversalAPIVelocityImplementation() {
        super(ServerObjectVelocityImplementation.class, ProxyObjectVelocityImplementation.class, ServerGroupObjectVelocityImplementation.class, ProxyGroupObjectVelocityImplementation.class, PlayerObjectVelocityImplementation.class, BaseObjectVelocityImplementation.class, CordObjectVelocityImplementation.class);
    }
}
