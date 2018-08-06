package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;

public class TimoCloudUniversalAPICordImplementation extends TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    public TimoCloudUniversalAPICordImplementation() {
        super(ServerObjectCordImplementation.class, ProxyObjectCordImplementation.class, ServerGroupObjectCordImplementation.class, ProxyGroupObjectCordImplementation.class, PlayerObjectCordImplementation.class, BaseObjectCordImplementation.class, CordObjectCordImplementation.class);
    }

}
