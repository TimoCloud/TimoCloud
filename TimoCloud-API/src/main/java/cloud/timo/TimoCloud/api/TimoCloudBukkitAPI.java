package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ServerObject;

/**
 * Use {@link TimoCloudAPI#getBukkitAPI()} to get an instance of this API
 */
public interface TimoCloudBukkitAPI {

    /**
     * @return The server you are on as ServerObject
     */
    ServerObject getThisServer();

}
