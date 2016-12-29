package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.Main;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Timo on 28.12.16.
 */
public class SignManager {
    private FileConfiguration config;
    private FileConfiguration layoutsConfig;
    private Map<Location, String> signs;

    public SignManager() {
        load();
    }

    public void load() {
        signs = new HashMap<>();
        config = Main.getInstance().getFileManager().getSigns();
        layoutsConfig = Main.getInstance().getFileManager().getSignLayouts();
        Set<String> servers = config.getKeys(false);
        for (String server : servers) {
            List<Location> locations = getSigns(server);
            for (Location location : locations) {
                signs.put(location, server);
            }
        }
        Main.log("Successfully loaded signs!");
    }

    public void updateSigns() {
        for (Location location : signs.keySet()) {
            writeSign(location, signs.get(location));
        }
    }

    public List<Location> getSigns(String server) {
        return (List<Location>) config.getList(server + ".locations", new ArrayList<Location>());
    }

    public void setSigns(String server, List<Location> locations) {
        config.set(server + ".locations", locations);
        File dir = new File("../../templates/" + Main.getInstance().getGroupByServer(Main.getInstance().getServerName()) + "/plugins/TimoCloud/");
        File signs = new File(dir, "signs.yml");
        dir.mkdirs();
        try {
            if (!signs.exists()) {
                signs.createNewFile();
            }
            config.save(signs);
            config = YamlConfiguration.loadConfiguration(signs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSign(String server, Location location) {
        List<Location> locations = getSigns(server);
        if (locations.contains(location)) {
            return;
        }
        locations.add(location);
        setSigns(server, locations);
        signs.put(location, server);
    }

    public void writeSign(Location location, String server) {
        Block block = location.getWorld().getBlockAt(location);
        BlockState state = block.getState();
        if (! (state instanceof Sign)) {
            Main.log("Error: block could not be cast to sign: " + location);
            Main.log("Material: " + block.getType());
            return;
        }
        Sign sign = (Sign) state;
        for (int i = 0; i<4; i++) {
            try {
                sign.setLine(i, replace(layoutsConfig.getString(Main.getInstance().getGroupByServer(server) + ".layouts." + Main.getInstance().getOtherServerPingManager().getState(server) + "." + (i + 1)), server));
            } catch (Exception e) {
                Main.log("Could not find layout " + Main.getInstance().getOtherServerPingManager().getState(server) + " in group " + Main.getInstance().getGroupByServer(server));
            }
        }
        sign.update();
    }

    public String replace(String string, String server) {
        return string
                .replace("%server_name%", server)
                .replace("%current_players%", Bukkit.getOnlinePlayers().size() + "")
                .replace("%max_players%", Bukkit.getMaxPlayers() + "")
                .replace("%state%", Main.getInstance().getOtherServerPingManager().getState(server))
                .replace("%extra%", Main.getInstance().getOtherServerPingManager().getExtra(server))
                .replace("&", "§")
                ;
    }
}
