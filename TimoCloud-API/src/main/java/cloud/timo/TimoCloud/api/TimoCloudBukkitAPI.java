package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ServerObject;

/**
 * Use {@link TimoCloudAPI#getBukkitInstance()} to get an instance of this class
 */
public interface TimoCloudBukkitAPI {

    /**
     * @return The server you are on as ServerObject
     */
    ServerObject getThisServer();

}
