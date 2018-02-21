package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;

public class TimoCloudUniversalAPIBungeeImplementation extends TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    public TimoCloudUniversalAPIBungeeImplementation() {
        super(ServerObjectBungeeImplementation.class, ProxyObjectBungeeImplementation.class, ServerGroupObjectBungeeImplementation.class, ProxyGroupObjectBungeeImplementation.class, PlayerObjectBungeeImplementation.class, CordObjectBungeeImplementation.class);
    }
}
