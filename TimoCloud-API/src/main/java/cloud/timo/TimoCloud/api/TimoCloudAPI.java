package cloud.timo.TimoCloud.api;

public class TimoCloudAPI {

    private static TimoCloudUniversalAPI timoCloudUniversalAPI;
    private static TimoCloudBukkitAPI timoCloudBukkitAPI;

    /**
     * Do not use this method. This will be done by TimoCloud
     */
    public static void setUniversalImplementation(TimoCloudUniversalAPI timoCloudUniversalAPI) {
        TimoCloudAPI.timoCloudUniversalAPI = timoCloudUniversalAPI;
    }

    /**
     * You can use this API on both, BungeeCord and Bukkit
     * @return Universal API instance
     */
    public static TimoCloudUniversalAPI getUniversalInstance() {
        return timoCloudUniversalAPI;
    }

    /**
     * Do not use this method. This will be done by TimoCloud
     */
    public static void setBukkitImplementation(TimoCloudBukkitAPI implementation) {
        timoCloudBukkitAPI = implementation;
    }

    /**
     * This API is for Bukkit plugins
     * @return Bukkit API instance
     */
    public static TimoCloudBukkitAPI getBukkitInstance() {
        return timoCloudBukkitAPI;
    }
}
