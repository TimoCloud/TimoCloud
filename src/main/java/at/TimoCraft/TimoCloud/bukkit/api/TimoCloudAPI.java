package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.bukkit.Main;

/**
 * Created by Timo on 28.12.16.
 */
public class TimoCloudAPI {
    private static String state = "ONLINE";
    private static String extra = "";

    public static String getServerName() {
        return Main.getInstance().getServerName();
    }

    public static String getState() {
        return state;
    }

    public static void setState(String state) {
        TimoCloudAPI.state = state;
        Main.getInstance().getBukkitSocketMessageManager().sendMessage("SETSTATE", state);
    }

    public static String getExtra() {
        return extra;
    }

    public static void setExtra(String extra) {
        TimoCloudAPI.extra = extra;
        Main.getInstance().getBukkitSocketMessageManager().sendMessage("SETEXTRA", extra);
    }
}
