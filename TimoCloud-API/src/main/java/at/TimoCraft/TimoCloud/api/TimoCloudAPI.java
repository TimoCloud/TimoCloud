package at.TimoCraft.TimoCloud.api;

/**
 * Created by Timo on 05.03.17.
 */
public class TimoCloudAPI {
    private static TimoCloudBukkitAPI timoCloudBukkitAPI;

    /**
     * Do not use this!!! This will be done by TimoCloud
     */
    public static void setBukkitImplementation(TimoCloudBukkitAPI implementation) {
        timoCloudBukkitAPI = implementation;
    }

    /**
     * Use this to work with the TimoCloud bukkit API
     * @return API instance
     */
    public static TimoCloudBukkitAPI getBukkitInstance() {
        return timoCloudBukkitAPI;
    }
}
