package cloud.timo.TimoCloud.bukkit.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.helpers.JsonHelper;
import cloud.timo.TimoCloud.bukkit.signs.SignInstance;
import cloud.timo.TimoCloud.bukkit.signs.SignLayout;
import cloud.timo.TimoCloud.bukkit.signs.SignParseException;
import cloud.timo.TimoCloud.bukkit.signs.SignTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class SignManager {

    private List<SignTemplate> signTemplates;
    private List<SignInstance> signInstances;
    private int updates = 0;

    public SignManager() {
        load();
    }

    public void load() {
        loadSignTemplates();
        loadSignInstances();

        TimoCloudBukkit.getInstance().info("Successfully loaded signs!");
    }

    private void loadSignTemplates() {
        signTemplates = new ArrayList<>();
        FileConfiguration config = TimoCloudBukkit.getInstance().getFileManager().getSignTemplates();
        for (String template : config.getKeys(false)) {
            Map<String, SignLayout> layouts = new HashMap<>();
            try {
                for (String layout : config.getConfigurationSection(template + ".layouts").getKeys(false)) {
                    List<String>[] lines = Arrays.copyOf(new Object[4], 4, List[].class);
                    for (int i = 0; i < 4; i++) {
                        String line = config.getString(template + ".layouts." + layout + ".lines." + (i + 1));
                        if (line == null)
                            throw new SignParseException("Line " + (i + 1) + " (signTemplates.yml section '" + template + ".layouts.lines." + (i + 1) + ") is not defined.");
                        lines[i] = Arrays.asList(line.split(";"));
                    }
                    Material signBlockMaterial = null;
                    int signBlockData = 0;
                    try {
                        String materialString = config.getString(template + ".layouts." + layout + ".signBlockMaterial");
                        if (materialString != null) materialString = materialString.toUpperCase();
                        signBlockMaterial = Material.getMaterial(materialString);
                        signBlockData = config.getInt(template + ".layouts." + layout + ".signBlockData");
                    } catch (Exception e) {
                        throw new SignParseException("Invalid signBlockMaterial or signBlockData. Please check https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for a list of valid materials. SignBlockData has to be a valid integer (number)");
                    }

                    layouts.put(layout, new SignLayout(
                            lines,
                            config.getLong(template + ".layouts." + layout + ".updateSpeed"),
                            signBlockMaterial,
                            signBlockData
                    ));
                }
                if (!layouts.containsKey("Default")) {
                    throw new SignParseException("Template " + template + " does not have a default layout 'Default'. Please add it and type /signs reload");
                }
                signTemplates.add(new SignTemplate(template, layouts));
            } catch (Exception e) {
                TimoCloudBukkit.getInstance().severe("Could not parse sign template &e" + template + "&c. Please check your &esignTemplates.yml&c.");
                e.printStackTrace();
            }
        }
    }

    private void loadSignInstances() {
        signInstances = new ArrayList<>();
        try {
            JSONArray jsonArray = TimoCloudBukkit.getInstance().getFileManager().getSignInstances();
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                signInstances.add(new SignInstance(
                        JsonHelper.locationFromJson((JSONObject) jsonObject.get("location")),
                        (String) jsonObject.get("target"),
                        (String) jsonObject.get("template"),
                        getSignTemplate((String) jsonObject.get("template")),
                        (boolean) jsonObject.get("dynamic"),
                        ((Number) jsonObject.get("priority")).intValue()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSignInstances() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (SignInstance signInstance : signInstances) {
                Map<String, Object> properties = new LinkedHashMap<>();
                properties.put("location", JsonHelper.locationToJson(signInstance.getLocation()));
                properties.put("target", signInstance.getTarget());
                properties.put("template", signInstance.getTemplateName());
                properties.put("dynamic", signInstance.isDynamic());
                properties.put("priority", signInstance.getPriority());
                jsonArray.add(new JSONObject(properties));
            }
            TimoCloudBukkit.getInstance().getFileManager().saveSignInstances(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SignTemplate getSignTemplate(String name) {
        for (SignTemplate signTemplate : signTemplates) if (signTemplate.getName().equals(name)) return signTemplate;
        for (SignTemplate signTemplate : signTemplates)
            if (signTemplate.getName().equalsIgnoreCase(name)) return signTemplate;
        if (!"Default".equalsIgnoreCase(name)) return getSignTemplate("Default");
        return null;
    }

    private SignTemplate templateNotFound(String name) {
        List[] lines = {
                Arrays.asList("&cCould not find"),
                Arrays.asList("&ctemplate"),
                Arrays.asList(""),
                Arrays.asList(name)
        };
        Map<String, SignLayout> layouts = new HashMap<>();
        layouts.put("Default", new SignLayout(lines, -1, null, 0));
        return new SignTemplate("TemplateNotFound", layouts);
    }

    public void updateSigns() {
        if (TimoCloudAPI.getUniversalInstance().getServerGroups() == null) return;
        List<SignInstance> dynamicInstances = new ArrayList<>();
        for (SignInstance signInstance : signInstances) {
            if (signInstance.isDynamic()) {
                dynamicInstances.add(signInstance);
            } else {
                processStaticSign(signInstance);
            }
        }
        processDynamicSigns(dynamicInstances);
        updates++;
    }

    private void processDynamicSigns(List<SignInstance> signInstances) {
        Map<ServerGroupObject, List<SignInstance>> groups = new HashMap<>();
        for (SignInstance signInstance : signInstances) {
            ServerGroupObject group = TimoCloudAPI.getUniversalInstance().getServerGroup(signInstance.getTarget());
            groups.computeIfAbsent(group, k -> new ArrayList<>());
            groups.get(group).add(signInstance);
        }
        for (ServerGroupObject group : groups.keySet()) processDynamicSignsPerGroup(group, groups.get(group));
    }

    private boolean isSignActive(SignInstance signInstance) {
        return signInstance.isActive() && signInstance.getLocation().getBlock().getState() instanceof Sign;
    }

    private void processDynamicSignsPerGroup(ServerGroupObject group, List<SignInstance> signInstances) {
        if (group == null) return;
        signInstances = signInstances.stream().filter(this::isSignActive).collect(Collectors.toList());

        List<ServerObject> targets = new ArrayList<>();
        for (ServerObject serverObject : group.getServers())
            if (!group.getSortOutStates().contains(serverObject.getState())) targets.add(serverObject);

        List<SignInstance> withPriority = signInstances.stream().filter((signInstance) -> signInstance.getPriority() != 0).collect(Collectors.toList());
        List<SignInstance> withoutPriority = signInstances.stream().filter((signInstance) -> signInstance.getPriority() == 0).collect(Collectors.toList());
        withPriority.sort(Comparator.comparing(SignInstance::getPriority));
        Integer lastPriority = null;
        int step = -1;
        for (SignInstance signInstance : withPriority) {
            if (signInstance == null) return;
            if (lastPriority == null || signInstance.getPriority() != lastPriority) {
                step++;
                lastPriority = signInstance.getPriority();
            }
            writeSign(step < targets.size() ? targets.get(step) : null, step < targets.size() ? signInstance.getTemplate() : getSignTemplate("NoFreeServerFound"), signInstance);
        }

        step = -1;
        withoutPriority.sort((o1, o2) -> {
            try {
                org.bukkit.material.Sign sign1 = (org.bukkit.material.Sign) o1.getLocation().getBlock().getState().getData();
                org.bukkit.material.Sign sign2 = (org.bukkit.material.Sign) o2.getLocation().getBlock().getState().getData();
                if (!sign1.getFacing().equals(sign2.getFacing())) return 0;
                if (o1.getLocation().getBlockY() != o2.getLocation().getBlockY())
                    return o2.getLocation().getBlockY() - o1.getLocation().getBlockY();
                switch (sign1.getFacing()) {
                    case NORTH:
                        return o2.getLocation().getBlockX() - o1.getLocation().getBlockX();
                    case SOUTH:
                        return o1.getLocation().getBlockX() - o2.getLocation().getBlockX();
                    case EAST:
                        return o2.getLocation().getBlockZ() - o1.getLocation().getBlockZ();
                    case WEST:
                        return o1.getLocation().getBlockZ() - o2.getLocation().getBlockZ();
                    default:
                        return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
        for (SignInstance signInstance : withoutPriority) {
            if (signInstance == null) return;
            step++;
            writeSign(step < targets.size() ? targets.get(step) : null, step < targets.size() ? signInstance.getTemplate() : getSignTemplate("NoFreeServerFound"), signInstance);
        }
    }

    private void processStaticSign(SignInstance signInstance) {
        if (signInstance.isDynamic()) {
            TimoCloudBukkit.getInstance().severe("Fatal error: Wanted to process dynamic sign as static sign. This should never happen - please report this!");
            return;
        }
        writeSign(TimoCloudAPI.getUniversalInstance().getServer(signInstance.getTarget()), signInstance.getTemplate(), signInstance);
    }

    private void writeSign(ServerObject server, SignTemplate signTemplate, SignInstance signInstance) {
        if (!isSignActive(signInstance)) return;
        if (signTemplate == null) signTemplate = templateNotFound(signInstance.getTemplateName());
        signInstance.setTargetServer(server);
        SignLayout signLayout = signTemplate.getLayout(server == null ? "Default" : server.getState());
        Sign sign = (Sign) signInstance.getLocation().getBlock().getState();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, replace(signLayout.getLine(i).get(signInstance.getStep() % signLayout.getLine(i).size()), server));
        }
        sign.update();
        setSignBlock(signInstance, signLayout);

        if (signLayout.getUpdateSpeed() > 0 && updates % signLayout.getUpdateSpeed() == 0)
            signInstance.setStep(signInstance.getStep() + 1);
    }

    private void setSignBlock(SignInstance signInstance, SignLayout signLayout) {
        Material material = signLayout.getSignBlockMaterial();
        if (material == null) return;
        Block signBlock = signInstance.getLocation().getBlock();
        Block attachedTo = getSignBlockAttached(signBlock);
        if (attachedTo == null) return;
        attachedTo.setType(material);
        try {
            Block.class.getMethod("setData", byte.class).invoke(attachedTo, (byte) signLayout.getSignBlockData());
        } catch (Exception e) {}
    }

    private Block getSignBlockAttached(Block signBlock) {
        if (! signBlock.getType().equals(Material.WALL_SIGN)) return null;
        return signBlock.getRelative(((org.bukkit.material.Sign) signBlock.getState().getData()).getAttachedFace());
    }

    public String replace(String string, ServerObject server) {
        if (server == null) return ChatColor.translateAlternateColorCodes('&', string);
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("%name%", server.getName())
                .replace("%server_name%", server.getName())
                .replace("%current_players%", Integer.toString(server.getOnlinePlayerCount()))
                .replace("%max_players%", Integer.toString(server.getMaxPlayerCount()))
                .replace("%state%", server.getState())
                .replace("%extra%", server.getExtra())
                .replace("%motd%", server.getMotd())
                .replace("%map%", server.getMap()));
    }

    private SignInstance getSignInstanceByLocation(Location location) {
        for (SignInstance signInstance : signInstances)
            if (signInstance.getLocation().equals(location)) return signInstance;
        return null;
    }

    public boolean signExists(Location location) {
        return getSignInstanceByLocation(location) != null;
    }

    public void onSignClick(Player player, Location location) {
        SignInstance signInstance = getSignInstanceByLocation(location);
        if (signInstance == null ||signInstance.getTarget() == null) return;
        TimoCloudBukkit.getInstance().sendPlayerToServer(player, signInstance.getTargetServer().getName());
    }

    public void addSign(Location location, String target, String template, int priority, Player player) {
        SignInstance existing = getSignInstanceByLocation(location);
        if (existing != null) signInstances.remove(existing);
        if (template.equals("")) template = "Default";
        SignTemplate signTemplate = getSignTemplate(template);
        if (signTemplate == null) {
            BukkitMessageManager.sendMessage(player, "&cError while creating sign: Could not find template &e" + template + "&c.");
            return;
        }
        boolean dynamic;
        if (TimoCloudAPI.getUniversalInstance().getServerGroup(target) != null) dynamic = true;
        else if (TimoCloudAPI.getUniversalInstance().getServer(target) != null) dynamic = false;
        else {
            BukkitMessageManager.sendMessage(player, "&cError while creating sign: Could not find group or server called &e" + target + "&c.");
            return;
        }
        signInstances.add(new SignInstance(location, target, template, signTemplate, dynamic, priority));
        BukkitMessageManager.sendMessage(player, "&aSuccessfully added sign. Please check the parsed data is correct: " +
                "\n  &eTarget&6: &3 " + target +
                "\n  &eIsGroup&6: &3 " + dynamic +
                "\n  &eTemplate&6: &3 " + signTemplate.getName() +
                "\n  &ePriority&6: &3 " + priority
        );
        saveSignInstances();
    }

    public void lockSign(Location location) {
        SignInstance signInstance = getSignInstanceByLocation(location);
        if (signInstance == null) return;
        signInstance.setActive(false);
    }

    public void unlockSign(Location location) {
        SignInstance signInstance = getSignInstanceByLocation(location);
        if (signInstance == null) return;
        signInstance.setActive(true);
    }

}
