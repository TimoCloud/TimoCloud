package at.TimoCraft.TimoCloud.api;

/**
 * Created by Timo on 05.03.17.
 */
public class TimoCloudAPI {

    private static TimoCloudUniversalAPI timoCloudUniversalAPI;
    private static TimoCloudBukkitAPI timoCloudBukkitAPI;

    /**
     * Do not use this method!!! This will be done by TimoCloud
     */
    public static void setUniversalImplementation(TimoCloudUniversalAPI timoCloudUniversalAPI) {
        TimoCloudAPI.timoCloudUniversalAPI = timoCloudUniversalAPI;
    }

    /**
     * You can use this API on both, BungeeCord and Bukkit!
     * @return Universal API instance
     */
    public static TimoCloudUniversalAPI getUniversalInstance() {
        return timoCloudUniversalAPI;
    }

    /**
     * Do not use this method !!! This will be done by TimoCloud
     */
    @Deprecated
    public static void setBukkitImplementation(TimoCloudBukkitAPI implementation) {
        timoCloudBukkitAPI = implementation;
    }

    /**
     * @deprecated Do not use this instance anymore! It will be removed soon and will break your plugin. Use {@link #getUniversalInstance()} instead
     * @return !!!OLD!!! Bukkit API instance
     */
    @Deprecated
    public static TimoCloudBukkitAPI getBukkitInstance() {
        return timoCloudBukkitAPI;
    }
}
