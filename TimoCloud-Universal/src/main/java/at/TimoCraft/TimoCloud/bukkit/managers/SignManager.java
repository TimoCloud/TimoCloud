package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import at.TimoCraft.TimoCloud.utils.ServerToGroupUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;

import java.io.File;
import java.util.*;

/**
 * Created by Timo on 28.12.16.
 */
public class SignManager {
    private FileConfiguration signsConfig;
    private FileConfiguration dynamicSignsConfig;
    private FileConfiguration layoutsConfig;
    private Map<Location, String> signs;
    private Map<String, List<Location>> dynamicSigns;

    public SignManager() {
        load();
    }

    public void load() {
        signs = new HashMap<>();
        dynamicSigns = new HashMap<>();

        signsConfig = TimoCloudBukkit.getInstance().getFileManager().getSigns();
        dynamicSignsConfig = TimoCloudBukkit.getInstance().getFileManager().getDynamicSigns();
        layoutsConfig = TimoCloudBukkit.getInstance().getFileManager().getSignLayouts();

        Set<String> servers = signsConfig.getKeys(false);
        for (String server : servers) {
            List<Location> locations = getSigns(server);
            for (Location location : locations) {
                signs.put(location, server);
            }
        }

        Set<String> groups = dynamicSignsConfig.getKeys(false);
        for (String group : groups) {
            List<Location> locations = getDynamicSigns(group);
            for (Location location : locations) {
                dynamicSigns.putIfAbsent(group, new ArrayList<>());
                List<Location> l = dynamicSigns.get(group);
                l.add(location);
                dynamicSigns.put(group, l);
            }
        }
        TimoCloudBukkit.log("Successfully loaded signs!");
    }

    public void updateSigns() {
        ArrayList<Location> locations = new ArrayList<>(signs.keySet());
        for (Location location : locations) {
            writeSign(location, signs.get(location), false);
        }
        ArrayList<String> groups = new ArrayList<>(dynamicSigns.keySet());
        for (String group : groups) {
            int i = 0;
            List<Location> dynamicLocations = (ArrayList) ((ArrayList) dynamicSigns.get(group)).clone();
            for (Location location : dynamicLocations) {
                i++;
                boolean found = false;
                String free = "NotFound";
                for (int x = i; x <= 100 && !found; x++) {
                    String name = group + "-" + x;
                    if (shouldBeSortedOut(TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(name), group)) {
                        continue;
                    }
                    free = name;
                    found = true;
                    i = x;
                }
                writeSign(location, free, true);
                signs.put(location, free);
            }
        }
    }

    public String getServerOnSign(Location location) {
        return signs.get(location);
    }

    public boolean shouldBeSortedOut(String state, String group) {
        return layoutsConfig.getStringList(group + ".sortOut").contains(state);
    }

    public boolean canSpectate(String state, String group) {
        return layoutsConfig.getStringList(group + ".spectate").contains(state);
    }

    public List<Location> getSigns(String server) {
        return (List<Location>) signsConfig.getList(server + ".locations", new ArrayList<Location>());
    }

    public List<Location> getDynamicSigns(String group) {
        return (List<Location>) dynamicSignsConfig.getList(group + ".locations", new ArrayList<Location>());
    }

    public List<Location> getSpectateSigns(String group) {
        return (List<Location>) dynamicSignsConfig.getList(group + ".spectateSigns", new ArrayList<Location>());
    }

    public void setSigns(String name, List<Location> locations, boolean dynamic) {
        FileConfiguration config = dynamic ? dynamicSignsConfig : signsConfig;
        config.set(name + ".locations", locations);
        File signs = dynamic ? TimoCloudBukkit.getInstance().getFileManager().getDynamicSignsFile() : TimoCloudBukkit.getInstance().getFileManager().getSignsFile();
        try {
            config.save(signs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGroup(String name) {
        if (!name.contains("-")) return true;
        try {
            Integer.parseInt(name.split("-")[name.split("-").length - 1]);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public void addSign(String name, Location location) {
        List<Location> locations;
        if (isGroup(name)) {
            dynamicSigns.putIfAbsent(name, new ArrayList<>());
            locations = dynamicSigns.get(name);
        } else {
            locations = getSigns(name);
        }
        if (locations.contains(location)) {
            return;
        }
        String s = "";
        locations.add(location);
        setSigns(name, locations, isGroup(name));
        if (isGroup(name)) {
            dynamicSigns.put(name, locations);
        } else {
            signs.put(location, name);
        }
    }

    public void writeSign(Location location, String server, boolean dynamic) {
        String group = ServerToGroupUtil.getGroupByServer(server);
        Block block = location.getWorld().getBlockAt(location);
        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            if (dynamic) {
                List<Location> locations = dynamicSigns.get(group);
                locations.remove(location);
                dynamicSigns.put(group, locations);
            } else {
                signs.remove(location);
            }
            setSigns(dynamic ? group : server, dynamic ? dynamicSigns.get(group) : new ArrayList<>(signs.keySet()), dynamic);
            TimoCloudBukkit.log("Removed sign at " + location + " because it did not exist anymore.");
            return;
        }

        Sign sign = (Sign) state;

        String serverState = TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server);

        for (int i = 0; i < 4; i++) {
            try {
                sign.setLine(i, replace(layoutsConfig.getString(ServerToGroupUtil.getGroupByServer(server) + ".layouts." + serverState + "." + (i + 1)), server));
            } catch (Exception e) {
                TimoCloudBukkit.log("Could not find layout " + TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server) + " in group " + ServerToGroupUtil.getGroupByServer(server));
                System.out.println(ServerToGroupUtil.getGroupByServer(server) + ".layouts." + TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server) + "." + (i + 1));
                e.printStackTrace();
            }
        }
        sign.update();
        //changeSignBlock(sign, serverState);
    }

    private void changeSignBlock(Sign sign, String state) {
        if (!sign.getBlock().getType().equals(Material.WALL_SIGN)) return;
        if (!TimoCloudBukkit.getInstance().getFileManager().getSignBlocks().getKeys(false).contains(state)) return;
        String color = TimoCloudBukkit.getInstance().getFileManager().getSignBlocks().getString(state + ".color");
        Block behind = getBlockBehindSign(sign.getBlock());
        behind.setType(Material.CLAY);
    }

    private Block getBlockBehindSign(Block signBlock){
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) signBlock.getState();
        if(! signBlock.getType().equals(org.bukkit.Material.WALL_SIGN)) return null;
        switch (sign.getFacing()) {
            case NORTH: return signBlock.getLocation().clone().add(0, 0, -1).getBlock();
            case SOUTH: return signBlock.getLocation().clone().add(0, 0, 1).getBlock();
            case EAST: return signBlock.getLocation().clone().add(1, 0, 0).getBlock();
            case WEST: return signBlock.getLocation().clone().add(-1, 0, 0).getBlock();
            default: TimoCloudBukkit.log("Error: Unknown sign facing: " + sign.getFacing() + ". Please report this.");
        }
        return null;
    }

    public String replace(String string, String server) {
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("%server_name%", server)
                .replace("%current_players%", TimoCloudBukkit.getInstance().getOtherServerPingManager().getCurrentPlayers(server) + "")
                .replace("%max_players%", TimoCloudBukkit.getInstance().getOtherServerPingManager().getMaxPlayers(server) + "")
                .replace("%state%", TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server))
                .replace("%extra%", TimoCloudBukkit.getInstance().getOtherServerPingManager().getExtra(server))
                .replace("%motd%", TimoCloudBukkit.getInstance().getOtherServerPingManager().getMotd(server))
                .replace("%map%", TimoCloudBukkit.getInstance().getOtherServerPingManager().getMap(server)));
    }

    public Map<Location, String> getSigns() {
        return signs;
    }
}
