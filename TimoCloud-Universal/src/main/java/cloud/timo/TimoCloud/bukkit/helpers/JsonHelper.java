package cloud.timo.TimoCloud.bukkit.helpers;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.json.JsonObjectBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class JsonHelper {

    public static JsonObject locationToJson(Location location) {
        return JsonObjectBuilder.create()
                .set("worldUUID", location.getWorld().getUID().toString())
                .set("worldName", location.getWorld().getName())
                .set("x", location.getBlockX())
                .set("y", location.getBlockY())
                .set("z", location.getBlockZ())
                .toJsonObject();
    }

    public static Location locationFromJson(JsonObject jsonObject) {
        World world = Bukkit.getWorld(UUID.fromString(jsonObject.get("worldUUID").getAsString()));
        if (world == null) world = Bukkit.getWorld(jsonObject.get("worldName").getAsString());
        if (world == null) {
            TimoCloudBukkit.getInstance().severe("Could not find world '" + jsonObject.get("worldName") + "'.");
            return null;
        }
        return new Location(world, jsonObject.get("x").getAsInt(), jsonObject.get("y").getAsInt(), jsonObject.get("z").getAsInt());
    }

}
