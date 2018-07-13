package cloud.timo.TimoCloud.api;

public class TimoCloudAPI {

    private static TimoCloudUniversalAPI universalAPI;
    private static TimoCloudBukkitAPI bukkitAPI;
    private static TimoCloudBungeeAPI bungeeAPI;
    private static TimoCloudCoreAPI coreAPI;
    private static TimoCloudEventAPI eventAPI;
    private static TimoCloudMessageAPI messageAPI;

    private TimoCloudAPI() {};

    /**
     * @return Whether the TimoCloud API is available
     */
    public static boolean isEnabled() {
        return getUniversalAPI() != null;
    }

    /**
     * You can use this API on both, BungeeCord and Bukkit
     *
     * @return Universal API instance
     * @deprecated Use {@link #getUniversalAPI()} instead
     */
    @Deprecated
    public static TimoCloudUniversalAPI getUniversalInstance() {
        return universalAPI;
    }

    /**
     * This API is for Bukkit plugins
     *
     * @return Bukkit API instance
     * @deprecated Use {@link #getBukkitAPI()} instead
     */
    @Deprecated
    public static TimoCloudBukkitAPI getBukkitInstance() {
        return bukkitAPI;
    }

    /**
     * This API is for BungeeCord plugins
     *
     * @return Bungee API instance
     * @deprecated Use {@link #getBungeeAPI()} instead
     */
    @Deprecated
    public static TimoCloudBungeeAPI getBungeeInstance() {
        return bungeeAPI;
    }

    /**
     * This API can be used everywhere
     * It enables you to register listeners which will be notified when certain events happen
     * @return The event API instance
     * @deprecated Use {@link #getEventAPI()} instead     */
    @Deprecated
    public static TimoCloudEventAPI getEventImplementation() {
        return eventAPI;
    }

    /**
     * You can use this API everywhere (Bukkit, Bungee, Core, ...)
     *
     * @return Universal API instance
     */
    public static TimoCloudUniversalAPI getUniversalAPI() {
        return universalAPI;
    }

    /**
     * This API is for Bukkit plugins
     *
     * @return Bukkit API instance
     */
    public static TimoCloudBukkitAPI getBukkitAPI() {
        return bukkitAPI;
    }

    /**
     * This API is for BungeeCord plugins
     *
     * @return Bungee API instance
     */
    public static TimoCloudBungeeAPI getBungeeAPI() {
        return bungeeAPI;
    }

    /**
     * This API is for Core plugins
     *
     * @return Core API instance
     */
    public static TimoCloudCoreAPI getCoreAPI() {
        return coreAPI;
    }

    /**
     * The event API makes it possible to register listeners which will be notified on certain events
     * You can use this API everywhere (Bukkit, Bungee, Core, ...)
     * @return Event API instance
     */
    public static TimoCloudEventAPI getEventAPI() {
        return eventAPI;
    }

    /**
     * The message API provides methods to communicate within the TimoCloud network
     * You can use this API everywhere (Bukkit, Bungee, Core, ...)
     *
     * @return Message API instance
     */
    public static TimoCloudMessageAPI getMessageAPI() {
        return messageAPI;
    }
}
