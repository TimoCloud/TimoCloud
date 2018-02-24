package cloud.timo.TimoCloud.bukkit.helpers;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class JsonHelper {
    public static JSONObject locationToJson(Location location) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("worldUUID", location.getWorld().getUID().toString());
        map.put("worldName", location.getWorld().getName());
        map.put("x", Integer.toString(location.getBlockX()));
        map.put("y", Integer.toString(location.getBlockY()));
        map.put("z", Integer.toString(location.getBlockZ()));
        return new JSONObject(map);
    }

    public static Location locationFromJson(JSONObject jsonObject) {
        World world = Bukkit.getWorld(UUID.fromString((String) jsonObject.get("worldUUID")));
        if (world == null) world = Bukkit.getWorld((String) jsonObject.get("worldName"));
        if (world == null) {
            TimoCloudBukkit.getInstance().severe("Could not find world '" + jsonObject.get("worldName") + "'.");
            return null;
        }
        return new Location(world, Integer.parseInt((String) jsonObject.get("x")), Integer.parseInt((String) jsonObject.get("y")), Integer.parseInt((String) jsonObject.get("z")));
    }

}
