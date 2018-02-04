package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudBukkitAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

public class TimoCloudBukkitAPIImplementation implements TimoCloudBukkitAPI {

    @Override
    public ServerObject getThisServer() {
        return TimoCloudAPI.getUniversalInstance().getServer(TimoCloudBukkit.getInstance().getServerName());
    }
}
