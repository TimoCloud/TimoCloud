package cloud.timo.TimoCloud.api;

public class TimoCloudAPI {

    private static TimoCloudUniversalAPI timoCloudUniversalAPI;
    private static TimoCloudBukkitAPI timoCloudBukkitAPI;
    private static TimoCloudBungeeAPI timoCloudBungeeAPI;
    private static TimoCloudEventAPI timoCloudEventAPI;

    /**
     * @return Whether TimoCloud is present on the server
     */
    public static boolean isEnabled() {
        return getUniversalInstance() != null;
    }

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

    /**
     * Do not use this method. This will be done by TimoCloud
     */
    public static void setBungeeImplementation(TimoCloudBungeeAPI implementation) {
        timoCloudBungeeAPI = implementation;
    }

    /**
     * This API is for BungeeCord plugins
     * @return Bungee API instance
     */
    public static TimoCloudBungeeAPI getBungeeInstance() {
        return timoCloudBungeeAPI;
    }

    /**
     * Do not use this method. This will be done by TimoCloud
     */
    public static void setEventImplementation(TimoCloudEventAPI implementation) {
        timoCloudEventAPI = implementation;
    }

    /**
     * @return The event API instance
     */
    public static TimoCloudEventAPI getEventImplementation() {
        return timoCloudEventAPI;
    }
}
